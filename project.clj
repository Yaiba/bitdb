(defproject bitdb "BitDB"
  :description "Full stack Clojure exploring .bit database."
  :url "https://github.com/Yaiba/bitdb"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-core "1.9.6"]
                 [ring/ring-jetty-adapter "1.9.6"]
                 [ring/ring-defaults "0.3.4"]
                 [com.stuartsierra/component "1.1.0"]]
  :source-paths ["src/clj"]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler bitdb.handler/app}
  :main ^:skip-aot bitdb.core
  :profiles
  {:dev {:main user
         :source-paths ["dev"]
         :dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]
                        [org.clojure/tools.namespace "1.2.0"]
                        [com.stuartsierra/component.repl "1.0.0"]]}})
