(defproject brand "1.1.1"
  :description "Web service which provides Branding metadata."
  :url "http://brandws-example.herokuapp.com/"
  :license {:name "All Rights Reserved."}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [ring/ring-jetty-adapter "1.1.8"]
                 [ring/ring-devel "1.1.0"]
                 [ring-basic-authentication "1.0.1"]
                 [environ "0.2.1"]
                 [com.cemerick/drawbridge "0.0.6"]
                 [compojure "1.1.5"]
                 [incanter/incanter-core "1.4.1"]
                 [cheshire "5.0.2"] ]
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.8.3"]
            [environ/environ.lein "0.2.1"]]
  :hooks [environ.leiningen.hooks]
  :profiles {:production {:env {:production true}}}
  :main brand.core)
