(ns bitdb.graphql-handler
  (:require [ring.util.response :as resp]
            [ring.util.request :as req]
            [clojure.data.json :as json]
            [com.walmartlabs.lacinia :as lacinia]
            ))

(defn graphql-handler
  [request]
  (let [graphql-request (json/read-str (req/body-string request) :key-fn keyword)
        {:keys [query variables]} graphql-request
        result (lacinia/execute (:schema request) query variables nil)]
    (-> (json/write-str result)
        resp/response
        (resp/content-type "application/json"))))
