(ns yuna.core
  (:require [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [muuntaja.core :as m]
            [yuna.routes :as routes]))

(defn app [config]
  (ring/ring-handler
   (ring/router
    [["/swagger.json"
      {:get {:no-doc  true
             :swagger {:info     {:title       "Yuna Login Api "
                                  :description ""}
                       :basePath "/"}
             :handler (swagger/create-swagger-handler)}}]
     ["/api" (routes/login-route config)]]
    {:exception pretty/exception
     :data      {:coercion   reitit.coercion.spec/coercion
                 :muuntaja   m/instance
                 :middleware [swagger/swagger-feature
                              parameters/parameters-middleware
                              muuntaja/format-negotiate-middleware
                              muuntaja/format-response-middleware
                              exception/exception-middleware
                              muuntaja/format-request-middleware
                              coercion/coerce-response-middleware
                              coercion/coerce-request-middleware
                              multipart/multipart-middleware]}})
   (ring/routes
    (swagger-ui/create-swagger-ui-handler
     {:path   "/"
      :config {:operationsSorter "alpha"}})
    (ring/create-default-handler))))