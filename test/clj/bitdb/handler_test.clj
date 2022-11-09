(ns bitdb.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [bitdb.handler :refer :all]))

(comment (deftest test-app
           (testing "not-found route"
             (let [response (app (mock/request :get "/invalid"))]
               (is (= (:status response) 404))))))
