(ns bitdb.core
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [bitdb.system :refer [new-system]]))

(defn -main
  [& args]
  (component/start (new-system)))
