(ns bitdb.handler
  (:require [compojure.core :as compojure]
            [compojure.route :as compojure-route]
            [ring.util.response :as resp]
            [ring.util.request :as req]
            [clojure.data.json :as json]
            [com.walmartlabs.lacinia :as lacinia]
            [ring.middleware.content-type :as ct]
            [ring.middleware.params :as params]
            [ring.middleware.resource :as resource]
            ;[ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            ))

(defn request-ip
  ([request]
   {:status 200
    :header {"Content-type" "text/plain"}
    :body (:remote-addr request)})
  ([request respond raise]
   (respond (request-ip request))))

(defn graphql-handler
  [request]
  (let [graphql-request (json/read-str (req/body-string request) :key-fn keyword)
        {:keys [query variables]} graphql-request
        result (lacinia/execute (:schema request) query variables nil)]
    (-> (json/write-str result)
        resp/response
        (resp/content-type "application/json"))))

(compojure/defroutes app-routes
  (compojure/GET "/" [] "Hello BitDB ooooo")
  (compojure/GET "/req" request (str request))
  (compojure/POST "/graphql" request (graphql-handler request))
  (compojure-route/not-found "Not Found"))

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
      ;(wrap-defaults site-defaults)
      (resource/wrap-resource "static")
      ct/wrap-content-type
      (wrap-components components)))
