(ns bitdb.system
  (:require [ring.adapter.jetty :as jetty]
            [bitdb.handler :as handler]
            ;;[bitdb.config :refer [new-config]]
            [bitdb.database :refer [new-database]]
            [bitdb.web-server :refer [new-web-server]]
            [bitdb.graphql-schema :refer [new-schema-provider]]
            [com.stuartsierra.component :as component]))

(defn new-system
  [{:keys [bb-db bb-server bb-run-mode] :as config}]
  (merge (component/system-map)
         ;;(new-config)
         (new-schema-provider)
         (new-database bb-db)
         (new-web-server bb-server)))
