(defproject yuna "0.1.0-SNAPSHOT"
  :description "Login Api Yuna"
  :url "https://shadowbytefox.de"
  :license {:name "© ShadowByteFox 2022"
            :url "https://shadowbytefox.de"}
  :plugins [[nicheware/lein-gitlab-wagon "1.0.0"]]
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [metosin/reitit "0.3.9"]
                 [ring "1.7.1"]
                 [ring/ring-defaults "0.3.2"]
                 [buddy "2.0.0"]
                 [org.clojure/data.json "0.2.6"]
                 [com.layerware/hugsql "0.4.9"]
                 [org.postgresql/postgresql "42.2.2"]
                 [dwanascie/dwanascie-core "0.25.2"]
                 [postgre-types "0.0.4"]
                 [jdbc-ring-session "1.2"]
                 [org.clojure/data.json "0.2.6"]
                 [com.draines/postal "2.0.3"]
                 [cljs-ajax "0.7.5"]
                 [clj-http "3.9.1"]
                 [clj-time "0.15.1"]]
  :resource-paths ["resources"]
  :uberjar-name "yuna-standalone.jar"
  :main yuna.server)
