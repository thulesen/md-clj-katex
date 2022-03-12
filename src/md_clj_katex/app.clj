(ns md-clj-katex.app
  (:require [clojure.java.io :as io]
            [shadow.resource :refer [slurp-resource] :as rc]
            [hiccup.core :refer [html]]
            [clojure.string :as str]))

(defn all-files
  "Recursively finds all the files in the directory `dir`.
  `dir` should be a java.io.File which represents a directory.
  Returns a list of java.io.File's"
  [dir]
  (->> (.listFiles dir)
       (mapcat (fn [file]
                 (if (.isDirectory file)
                   (all-files file)
                   (list file))))))

(defn relativize
  "Returns string `path` st. `child` is (str `parent` `child`).
  Intended use when `parent` is an absolute path to a directory and `child` is
  an absolute path to a file or directory inside this directory."
  [parent child]
  (str/replace-first child (re-pattern parent) ""))

(defn extension
  "Returns the extension of file path `path`."
  [path]
  (re-find #"[^\\.]*$" path))

(defn replace-extension
  "Returns `path` but with the extension replaced by `new-ext`."
  [path new-ext]
  (str/replace-first path #"[^\\.]*$" new-ext))

(defn remove-extension
  "Removes the extension from `path`."
  [path]
  (str/replace-first path #"\.[^\.]*$" ""))

(defn handler
  "This http handler serves up a page that supports the app in app.cljs.
  No matter the URI, the returned html links to public/katex.css and includes
  the script public/main.js."
  [{:keys [uri http-roots http-config] :as req}]
  (let [level  (-> uri
                   (str/split #"/")
                   count)
        prefix (str/join (repeat level "../"))]
    {:status  200
     :headers {"content-type" "text/html; charset=utf-8"}
     :body    (html [:html
                     [:head
                      [:link {:rel  "stylesheet"
                              :type "text/css"
                              :href (str prefix "katex.css")}]]
                     [:div {:id "app"}]
                     [:script {:id  "main"
                               :src (str prefix "main.js")}]])}))

(defmacro markdown
  "Returns a map of relative paths of files in src/md to their contents.
  The paths are relative to src/md. More precisely, `path`` will be mapped to
  code `(rc/inline md/path)` which ensures that in the resulting ClojureScript
  code, the contents of these files will be available as the values of this map
  and shadow-cljs will reload their contents if they change."
  []
  (let [src-dir   (-> (io/resource ".")
                      io/as-file)
        md-dir    (-> (io/resource "md")
                      io/as-file)
        filenames (->> md-dir
                       all-files
                       (map #(.getAbsolutePath %))
                       (map (partial relativize (.getAbsolutePath src-dir))))]
    (into {} (for [path filenames]
               [(->> path
                     (relativize "/md")
                     remove-extension)
                `(rc/inline ~path)]))))
