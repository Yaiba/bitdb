(ns bitdb.handler
  (:require [compojure.core :as compojure]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [ring.middleware.content-type :as ct]
            [ring.middleware.params :as params]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn hello [request]
  (-> (resp/response "Hello BitDB")
      (resp/content-type "text/html")))
  ;; {:status 200
  ;;  :headers {"Content-Type" "text/html"}
  ;;  :body "Hello Ring"}

(defn request-ip
  ([request]
   {:status 200
    :header {"Content-type" "text/plain"}
    :body (:remote-addr request)})
  ([request respond raise]
   (respond (request-ip request))))

(compojure/defroutes app-routes
  (compojure/GET "/" [] "Hello BitDB")
  (route/not-found "Not Found"))

(defn components-request
  [request components]
  (merge request components))

(defn wrap-components [handler components]
  (fn
    ([request]
     (handler (components-request request components)))
    ([request respond raise]
     (handler (components-request request components) respond raise))))

(defn app [components]
  (-> app-routes
      (wrap-defaults site-defaults)
      (wrap-components components)))

