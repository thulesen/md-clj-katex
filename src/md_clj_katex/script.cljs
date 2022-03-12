(ns md-clj-katex.script
  (:require [md-clj-katex.markdown :refer [md->html]]
            ["fs" :as fs]
            ["path" :as path]
            ["minimist" :as parseArgs])
  (:require-macros [md-clj-katex.script :refer [head]]))

(defn add-css [html]
  (str "<!DOCTYPE html>"
       "<html>"
       (head)
       html
       "</html>"))

(defn process-file
  "Processes a file as markdown.
  This function reads the file at `inpath`, interprets its content as markdown
  and writes the resulting html to `outpath`."
  [inpath outpath]
  (->> (fs/readFileSync inpath "utf8")
       md->html
       add-css
       (fs/writeFileSync outpath)))

(defn process-dir
  "Processes all the files in a directory as markdown.
  This functions reads all the files in the directory at `inpath` (recursively),
  interprets their content as markdown and writes the resulting html files to
  the folder at `outpath` (creating it if necessary), respecting the directory
  structure of the input directory."
  [inpath outpath]
  (when (not (fs/existsSync outpath))
    (fs/mkdirSync outpath))
  (doseq [name (fs/readdirSync inpath)
          :let [inpath (path/join inpath name)
                outpath (path/join outpath name)]]
    (if (->> inpath
             fs/lstatSync
             .isFile)
      (let [outpath (-> outpath
                        path/parse
                        js->clj
                        (assoc "ext" ".html"
                               "base" nil)
                        clj->js
                        path/format)]
        (process-file inpath outpath))
      (process-dir inpath outpath))))

(defn main [inpath outpath]
  (let [args js/process.argv
        parsed-args (js->clj (parseArgs args)
                             :keywordize-keys true)
        positional-args (:_ parsed-args)]
    (if (or (:help parsed-args)
            (:h parsed-args)
            (not (= 4 (count args))))
      (do (println "Arguments: [--help] [-h] SOURCE DESTINATION")
          (println "If SOURCE is a file, interpret its contents as markdown and write the result
to DESTINATION. If SOURCE is a directory, do the same to all the files in it and
write the result to DESTINATION, preserving the directory structure."))
      (if (->> inpath fs/lstatSync .isFile)
        (process-file inpath outpath)
        (process-dir inpath outpath)))))
