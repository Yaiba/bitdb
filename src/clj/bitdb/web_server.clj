(ns bitdb.web-server
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [bitdb.handler :refer [app]]))

(defrecord WebServer [config http-server schema-provider datasource]
  component/Lifecycle
  (start [this]
    (println ";; Starting web server")
    (if http-server
      this
      (assoc this :http-server
             (jetty/run-jetty (app {:datasource datasource
                                    :schema (:schema schema-provider)})
                              {:port (:bb-jetty-port config)
                               :join? (:bb-jetty-join config)}))))
  (stop [this]
    (println ";; Stopping web server")
    (when http-server
      (.stop http-server))
    (assoc this :http-server nil)))

(defn new-web-server
  [config]
  {:server (component/using (map->WebServer {:config config})
                            [:datasource :schema-provider])})

