(ns yuna.func.login
  (:require [yuna.sql.sql-wraper :as sql-wrap]
            [clojure.tools.logging :as log]
            [yuna.helper.jwtservice :as jwt]
            [yuna.cryptevents.AES :as AES]))


(defn if-verification-done? [email password {:keys [profile-db password-seed]}]
  (try
    (first (sql-wrap/if-verification-done? profile-db {:email    email
                                                       :password (AES/encrypt password password-seed)}))
    (catch Exception e
      (prn e)
      {:status 200
       :body {:message "API ERROR"}})))


(defn loggin-user
  "Login function Influence"
  [{email :email password :password role :role} {:keys [profile-db password-seed jws-secret] :as config}]
  (try
    (let [mysql-response (first (sql-wrap/login profile-db {:email    email
                                                            :role role
                                                            :password (AES/encrypt password password-seed)}))
          verification? (if-verification-done? email password config)
          make-jwt (jwt/generate-signature {:id       (:id mysql-response)
                                            :username (:username mysql-response)
                                            :identity (:identity mysql-response)
                                            :language (keyword (:language mysql-response))
                                            :roles    role} jws-secret)]
      (if (not-empty mysql-response)
        (if verification?
          (if (= 1 (sql-wrap/save-jwt-token profile-db {:user_id   (:id mysql-response)
                                                        :jwt_token make-jwt}))
            (do
              (log/info "Login detected Email:" email " role:" role " valid: true")
              {:jwt      make-jwt
               :roles    role
               :identity (:identity mysql-response)
               :language (keyword (:language mysql-response))
               :login    true})
            (do (log/info "Login detected Email:" email " role:" role " valid: true")
                {:error "cant save jwt token pleas tray again!"}))
          {:status 200
           :body   {:warning "Email adresse wurde noch nicht bestätigt!"}})
        {:status 200
         :body   {:error "Anmeldung fehlgeschlagen Password oder Email falsch"}}))
    (catch Exception e
      (prn e)
      {:status 200
       :body {:message "API ERROR"}})))


(defn login-handler [params config]
  (try (let [session-data (loggin-user params config)]
         (if (:login session-data)
           {:status  200
            :headers {"jwt" (:jwt session-data)}
            :body    (dissoc session-data :jwt)}
           {:status (:status session-data)
            :body   (:body session-data)}))
       (catch Exception e
         (prn e)
         {:status 200
          :body {:message "API ERROR"}})))

(defn verification-jwt-token [jwt {:keys [profile-db jws-secret]}]
  (try (let [jwt-data (jwt/unsign-token jwt jws-secret)
             {:keys [id]} jwt-data]
         (if-not (empty? (sql-wrap/check-if-same-jwt profile-db {:jwt_token jwt
                                                                 :user_id   id}))
           {:status 200
            :body   {:jwt jwt-data
                     :valid true}}
           {:status 200
            :body   {:valid false}}))
       (catch Exception e
         (prn e)
         {:status 200
          :body {:message "API ERROR"}})))

(defn verification-a-email [code {:keys [profile-db]}]
  (try (if (empty? (sql-wrap/check-code-if-verification-done profile-db {:verification_code code}))
         (if (= 1 (sql-wrap/verification-a-email profile-db {:verification_code code}))
           {:status 200
            :body   {:tag     :verification_email
                     :code    0
                     :status  "success"
                     :message "Email verify!"}}
           {:status 200
            :body   {:tag     :verification_email
                     :code    1
                     :status  "danger"
                     :message "Wrong code!"}})
         {:status 200
          :body   {:tag     :verification_email
                   :code    2
                   :status  "warning"
                   :message "Code already used"}})
       (catch Exception e
         (prn e)
         {:status 200
          :body {:message "API ERROR"}})))


(defn valid-changeCode [user_id changeCode {:keys [profile-db]}]
  (let [valid? (-> (sql-wrap/valid-changeCode profile-db {:changeCode changeCode
                                                          :user_id    user_id})
                   first
                   :changecode
                   not-empty
                   boolean)]
    (if valid?
      {:status 200
       :body   {:accept valid?}}
      {:status 200
       :body {:accept valid?}})))

(defn valid-access [user-id view-code {:keys [profile-db]}]
  (try (let [viewCode (-> (sql-wrap/valid-access-sql profile-db {:id       user-id
                                                                 :viewCode view-code})
                          first
                          :viewcode)]
         (boolean (not-empty viewCode)))
       (catch Exception e
         (prn e)
         false)))
