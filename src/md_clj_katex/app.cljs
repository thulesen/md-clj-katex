(ns md-clj-katex.app
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [md-clj-katex.markdown :refer [md->html]]
            [shadow.resource :as rc])
  (:require-macros [md-clj-katex.app :as macros]))

(def markdown (macros/markdown))

(def source-path
  (.. js/window
      -location
      -pathname))

(defn app []
  [:div {:dangerouslySetInnerHTML {:__html (md->html (get markdown source-path))}}])

(defn ^:dev/after-load render []
  (rdom/render [app] (. js/document (getElementById "app"))))
