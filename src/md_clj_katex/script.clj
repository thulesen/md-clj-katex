(ns md-clj-katex.script
  (:require [hiccup.core :refer [html]]))

(defmacro head
  "Returns the head for a html document so it supports katex"
  []
  (html [:head
         [:link {:rel "stylesheet"
                 :href "https://cdn.jsdelivr.net/npm/katex@0.15.1/dist/katex.min.css"
                 :integrity "sha384-R4558gYOUz8mP9YWpZJjofhk+zx0AS11p36HnD2ZKj/6JR5z27gSSULCNHIRReVs"
                 :crossorigin "anonymous"}]]))
