(ns yuna.server
  (:require
   [dwanascie.system :as dwan]
   [yuna.init :refer [new-webserver]])
  (:gen-class))

(def definition {:service/name       "Yuna Platform"
                 :service/type       "Profile api"
                 :service/components {:webserver (new-webserver)}})

(defn -main [& args]
  (dwan/main definition args))
