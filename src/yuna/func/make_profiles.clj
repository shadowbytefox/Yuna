(ns yuna.func.make-profiles
  (:require [yuna.cryptevents.AES :as AES]
            [postal.core :as send-mail]
            [yuna.sql.sql-wraper :as sql-wrap]
            [clojure.string :as string])
  (:import (java.util UUID)))

(defn if-valid-email?
  [email]
  (let [pattern #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"]
    (not (nil? (and (string? email) (re-matches pattern email))))))

(defn Check-email [email {:keys [profile-db]}]
  (sql-wrap/check-email-exist profile-db {:email email}))

(defn create-user [{email                  :email
                    password               :password
                    #_#_verification_email_url :verification_email_url
                    role                  :role
                    password-second       :password-second}
                   {:keys [#_mail-host profile-db password-seed] :as config}]
  (try
    (if (if-valid-email? email)
      (let [viewcode (string/replace (str (UUID/randomUUID)) "-" "")
            changeCode (string/replace (str (UUID/randomUUID)) "-" "")
            verification_code (string/replace (str (UUID/randomUUID)) "-" "")
            #_#_email-body {:from    (:user mail-host)
                            :to      email
                            :subject "Wilkomemn bei CHANGE_ME"
                            :body    ""}]
        (if (and (empty? (Check-email email config))
                 (= password password-second))
          (let [sql (sql-wrap/create-account profile-db {:email             email
                                                         :viewcode          viewcode
                                                         :verification_code verification_code
                                                         :role              role
                                                         :changecode        changeCode
                                                         :password          (AES/encrypt password password-seed)
                                                         :username          (string/replace (str (UUID/randomUUID)) "-" "")})
                user_id (:id (first sql))]
            (if-not (nil? user_id)
              (let []
                {:status 200
                 :body   (merge {:tag           :register-account
                                 :code          1
                                 :status        "success"
                                 #_#_:email-status  (let [send-status (send-mail/send-message mail-host email-body)]
                                                      (if (= :SUCCESS (:error send-status))
                                                        "Email was sending"
                                                        "Cant send Email!"))
                                 :message       "Account has benn created!"})})
              {:status 200
               :body   {:tag     :register-account
                        :code    2
                        :status  "danger"
                        :message "Account cant create pls contact a Admin!"}}))
          {:status 200
           :body   {:tag     :register-account
                    :code    3
                    :status  "warning"
                    :message "Error Email already in use!"}}))
      {:status 200
       :body   {:tag     :register-account
                :code    4
                :status  "warning"
                :message "Email its not Valid!"}})
    (catch Exception e
      (prn e)
      {:status 200
       :body   {:error  "API ERROR!"
                :status "error"}})))


(defn make-new-changecode [user_id url {:keys [profile-db mail-host]}]
  (let [new-changecode (string/replace (str (UUID/randomUUID)) "-" "")
        user_email (:email (first (sql-wrap/get-user-email profile-db {:user_id user_id})))]
    (if (= 1 (sql-wrap/set-new-changecode profile-db {:user_id user_id
                                                      :changecode   new-changecode}))
      (try
        #_(send-mail/send-message mail-host
                                  {:from    (:user mail-host)
                                   :to      user_email
                                   :subject "Changecode"
                                   :body    (str "
                                     Hallo User! \n
                                     hier ist ihr Change Code:\n
                                     " url "?code=" new-changecode)})
        {:status 200
         :body   {:message "Ihre Email ist auf dem weg uns sollte bald bei ihnen ankommen"}}
        (catch Exception e
          (prn e)
          {:status 200
           :body   {:message "Es konnte keine Email gesendet werden bitte Kontaktieren sie einen Admin"}}))
      {:status 200
       :body   {:message "Es gab ein Fehler beim erstellen des Changecodes"}})))