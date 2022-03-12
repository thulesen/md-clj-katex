(ns md-clj-katex.markdown
  (:require ["katex" :as katex]
            [markdown.core :as md]
            [markdown.common :refer [make-separator]]
            [markdown.transformers :refer [transformer-vector]]
            [shadow.resource :as rc]
            [cljs.reader :refer [read-string]]))

(def macros
  "This is automatically updated when src/md/md_clj_katex/macros.edn changes."
  (read-string (rc/inline "./macros.edn")))

(defn katex-render
  "Interprets `text` as latex equation code and renders it as html using katex.
  If optional argument `inline?` is truthy, the equation code is rendered in
  display mode, i.e., in a way suitable for display on its own line. The value
  of `md-clj-katex.markdown/macros` is used to determine what latex macros
  should be used. It should be a map of strings to strings, the latex shorthand
  mapped to its expansion. Macros with arguments are not supported."
  [text & {:keys [inline?]
           :or   {inline? false}}]
  (katex/renderToString text (clj->js {:throwOnError false
                                       :displayMode  (not inline?)
                                       :macros       macros})))

(def katex-inline-transformer
  "Renders LaTeX code enclosed by $'s into non display mode math."
  (make-separator "$" "" ""
                  (fn [text state]
                    [(str "%" (katex-render text :inline? true) "%")
                     state])))

(defn katex-displaymode-transformer
  "Renders LaTeX code enclosed by $'s on its own line into display mode math."
  [text state]
  (let [match (re-matches #"^\$(.*)\$$" text)]
    (if match
      (let [[_ text] match]
        [(str "%" (katex-render text :inline false) "%")
         state])
      [text state])))

(defn md->html
  "Main function for turning markdown into html with katex support.
  This supports the syntax of markdown-clj and the following syntax:
  1. LaTeX equation code enclosed by $'s is turned into non display mode math.
  2. LaTeX equation code enclosed by $'s on its own line is turned into display
     mode math.
  The rendering makes use of an inhibit separator %."
  [md-text]
  (-> md-text
      (md/md->html :replacement-transformers (into [katex-displaymode-transformer
                                                    katex-inline-transformer]
                                                   transformer-vector)
                   :inhibit-separator "%")))
