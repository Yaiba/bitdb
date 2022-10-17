(ns bitdb.system
  (:require [ring.adapter.jetty :as jetty]
            [bitdb.handler :as handler]
            [bitdb.database :refer [new-database]]
            [bitdb.web-server :refer [new-web-server]]
            [com.stuartsierra.component :as component]))

(defn new-system
  "Constructs a system map"
  [{:keys [bb-db bb-server bb-run-mode] :as config}]
  (component/system-map
   :database (new-database bb-db)
   :server (component/using
            (new-web-server bb-server)
            [:database] ;; dependency
            )))
