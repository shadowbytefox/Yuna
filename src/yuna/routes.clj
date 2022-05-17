(ns yuna.routes
  (:require
   [yuna.func.make-profiles :as create-profile]
   [yuna.helper.jwtservice :as jwt]
   [yuna.func.login :as login]
   [reitit.ring :as ring]))

(defn login-route [config]
  ["/yuna"
   {:swagger {:tags ["Login"]}}
   ["/verification"
    ["/email"
     ["/:code" {:get {:summary    "Verification a email address"
                      :parameters {:path {:code string?}}
                      :handler    (fn [{{{:keys [code]} :path} :parameters}]
                                    (login/verification-a-email code config))}}]]
    ["/jwt" {:post {:summary    "check if JWT token valid"
                    :parameters {:body {:jwt string?}}
                    :handler    (fn [{{{:keys [jwt]} :body} :parameters}]
                                  (login/verification-jwt-token jwt config))}}]]
   ["/singin"
    {:post {:summary    "login a user"
            :parameters {:body {:email string?
                                :password string?
                                :role string?}}
            :handler    (fn [{{:keys [body]} :parameters}]
                          (login/login-handler body config))}}]
   ["/create"
    {:post {:summary    "Crate an Account"
            :parameters {:body {:email                  string?
                                :password               string?
                                :role                   string?
                                :verification_email_url string?
                                :password-second        string?}}
            :handler    (fn [{{:keys [body]} :parameters}]
                          (create-profile/create-user body config))}}]
   ["/codes"
    {:swagger {:tags ["Codes"]}
     :parameters {:header {:jwt string?}}
     :roles      #{:company :user :admin}
     :middleware [#(jwt/wrap-enforce-roles % ring/get-match config)]}
    ["/changecode" {:post {:summary    "send a changecode to user"
                           :parameters {:body {:url string?}}
                           :handler    (fn [{{{:keys [id url]} :body} :parameters}]
                                         (create-profile/make-new-changecode id url config))}}]
    ["/valid"
     ["/changecode" {:post {:summary    "check its change code its valid"
                            :parameters {:body {:change_code string?}}
                            :responses  {200 {:body {:accept boolean?}}
                                         401 {:body {:accept boolean?}}}
                            :handler    (fn [{{{:keys [id change_code]} :body} :parameters}]
                                          (login/valid-changeCode id change_code config))}}]]]])
