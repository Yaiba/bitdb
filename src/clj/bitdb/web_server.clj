(ns bitdb.web-server
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [bitdb.handler :refer [app]]))

(defrecord WebServer [config http-server database]
  component/Lifecycle
  (start [this]
    (println ";; Starting web server")
    (if http-server
      this
      (assoc this :http-server
             (jetty/run-jetty (app {:database database})
                              config))))
  (stop [this]
    (println ";; Stopping web server")
    (when http-server
      (.stop http-server))
    (assoc this :http-server nil)))

(defn new-web-server
  [config]
  (map->WebServer {:config config}))
