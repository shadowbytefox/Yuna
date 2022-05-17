(ns yuna.helper.jwtservice
  (:require [buddy.sign.jwt :as jwt]
            [yuna.sql.sql-wraper :as sql]
            [clojure.string :as string])
  (:import (java.util UUID)))

(defn generate-signature [key-map secret]
  (jwt/sign (assoc key-map :nonsense (string/replace (str (UUID/randomUUID)) "-" "")) secret))

(defn unsign-token [token secret]
  (try
    (jwt/unsign token secret)
    (catch Exception e
      (prn e)
      {:status 403
       :body   {:error "unable to open JWT pleas login!"}})))

(defn check-jwt-by-user [id jwt-token {:keys [profile-db]}]
  (boolean (not-empty (sql/check-if-same-jwt profile-db {:user_id id
                                                         :jwt_token jwt-token}))))

(defn wrap-enforce-roles [handler ringmatch {:keys [jws-secret] :as config}]
  (try (fn [{:keys [parameters] :as request}]
         (let [{:keys [header]} parameters
               jwt-token (:jwt header)
               open-jwt (unsign-token jwt-token jws-secret)
               required (some-> request ringmatch :data :roles)]
           (if (check-jwt-by-user (:id open-jwt) jwt-token config)
             (if-not (contains? required (keyword (:roles open-jwt)))
               (if (= 403 (:status open-jwt))
                 open-jwt
                 {:status 403
                  :body   "forbidden"})
               (handler (update-in request [:parameters :body] assoc
                                   :id (:id open-jwt)
                                   :identity (UUID/fromString (:identity open-jwt)))))
             {:status 403
              :body   "forbidden"})))
       (catch Exception e
         (prn e)
         {:status 200
          :body {:message "API ERROR"}})))