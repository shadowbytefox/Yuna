(ns yuna.cryptevents.AES
  (:require [yuna.cryptevents.entity :refer [decode-map-entity encode-map-entity]]
            [clojure.string :refer [lower-case]])
  (:import (javax.crypto Cipher KeyGenerator)
           (javax.crypto.spec SecretKeySpec)
           (java.security SecureRandom)
           (org.apache.commons.codec.binary Base64)))

(defn makebytes [s]
  (.getBytes s "UTF-8"))

(defn base64 [b]
  (Base64/encodeBase64String b))

(defn debase64 [s]
  (Base64/decodeBase64 (makebytes s)))

(defn get-raw-key [seed]
  (let [keygen (KeyGenerator/getInstance "AES")
        sr (SecureRandom/getInstance "SHA1PRNG")]
    (.setSeed sr (makebytes seed))
    (.init keygen 192 sr)
    (.. keygen generateKey getEncoded)))

(defn get-cipher [mode seed]
  (let [key-spec (SecretKeySpec. (get-raw-key seed) "AES")
        cipher (Cipher/getInstance "AES")]
    (.init cipher mode key-spec)
    cipher))

(defn encrypt [text key]
  (let [bytes (makebytes text)
        cipher (get-cipher Cipher/ENCRYPT_MODE key)]
    (base64 (.doFinal cipher bytes))))

(defn decrypt [text key]
  (let [cipher (get-cipher Cipher/DECRYPT_MODE key)]
    (String. (.doFinal cipher (debase64 text)))))

(defn convert-bool [value]
  (cond
    (= (lower-case value) "false") false
    (= (lower-case value) "true") true
    :else value))

(defn nil?->nodecrypt [v secret]
  (cond
    (boolean? v) v
    (or (nil? v) (empty? v)) ""
    :else (decrypt v secret)))

(defn encrypt-map [m secret]
  (reduce (fn [r [k v]]
            (if (map? v)
              (assoc r k (encrypt-map v secret))
              (assoc r k (encrypt (str v) secret)))) {} (encode-map-entity m)))

(defn decrypt-map [m secret]
  (decode-map-entity (reduce (fn [r [k v]]
                               (if (map? v)
                                 (assoc r k (decrypt-map  v secret))
                                 (assoc r k (nil?->nodecrypt (convert-bool v) secret)))) {} m)))