(defproject lambdar-steamfeeds "0.1.0-SNAPSHOT"
  :description "A feed generation service for Steam search results."
  :url "http://lambdar.info/steamfeeds/api"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.8"]
                 [enlive "1.1.5"]
                 [hiccup "1.0.5"]
                 [clj-rss "0.1.8"]
                 [cheshire "5.3.1"]
                 [ring/ring-core "1.3.0"]]
  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler lambdar-steamfeeds.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}
   :uberjar {:aot :all}}
  :min-lein-version "2.0.0")
