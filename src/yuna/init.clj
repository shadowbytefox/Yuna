(ns yuna.init
  (:require [yuna.core :refer [app]]
            [ring.adapter.jetty :as jetty]
            [com.stuartsierra.component :as component]
            [schema.core :as s]))

(defrecord websever [config]
  component/Lifecycle
  (start [component]
    (jetty/run-jetty (app config)
                     {:port  (:port config)
                      :join? false})
    component)
  (stop [component]
    component))

(defn new-webserver []
  (map->websever {:config-shape {:port          s/Num
                                 :aes-secret    s/Str
                                 :password-seed s/Str
                                 :jws-secret    s/Str
                                 :profile-db    {:dbtype   s/Str
                                                 :host     s/Str
                                                 :dbname   s/Str
                                                 :user     s/Str
                                                 :password s/Str}}
                  :config       {:port          8080
                                 :aes-secret    "CHANGE_ME"
                                 :jws-secret    "CHANGE_ME"
                                 :password-seed "CHANGE_ME"
                                 :profile-db    {:dbtype   "postgresql"
                                                 :host     "127.0.0.1"
                                                 :dbname   "profiles"
                                                 :user     "CHANGE_ME"
                                                 :password "CHANGE_ME"}}}))