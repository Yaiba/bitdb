(ns bitdb.system
  (:require [ring.adapter.jetty :as jetty]
            [bitdb.handler :as handler]
            [bitdb.database :refer [new-database]]
            [bitdb.web-server :refer [new-web-server]]
            [com.stuartsierra.component :as component]))

(defn new-system
  "Constructs a system map"
  [config]
  (let [{:keys [database server]} config]
    (component/system-map
     :database (new-database database)
     :server (component/using (new-web-server server)
              [:database] ;; dependency
              ))))
