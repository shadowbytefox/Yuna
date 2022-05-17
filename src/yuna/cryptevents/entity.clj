(ns yuna.cryptevents.entity
  (:require [clojure.string :as cloString]))

(defn html-char-encode [value]
  (if (string? value)
    (cloString/replace value
                       #"[^\w\s\\\/\"\'\[\]\.\,\?\!\-\+\#\@\%\:\;\&\*\(\)\=]"
                       {"ä" "&auml;"
                        "Ä" "&Auml;"
                        "ö" "&ouml;"
                        "Ö" "&Ouml;"
                        "ü" "&uuml;"
                        "Ü" "&Uuml;"
                        "ß" "&szlig;"
                        "€" "&euro;"
                        "&" "&amp;"
                        "<" "&lt;"
                        ">" "&gt;"
                        "©" "&copy;"
                        "•" "&bull;"
                        "™" "&trade;"
                        "®" "&reg;"
                        "§" "&sect;"})
    value))

(defn html-char-decode [value]
  (if (string? value)
    (cloString/replace value
                       #"&auml;|&Auml;|&ouml;|&Ouml;|&uuml;|&Uuml;|&szlig;|&euro;|&amp;|&lt;|&gt;|&copy;|&bull;|&trade;|&reg;|&sect;"
                       {"&auml;"  "ä"
                        "&Auml;"  "Ä"
                        "&ouml;"  "ö"
                        "&Ouml;"  "Ö"
                        "&uuml;"  "ü"
                        "&Uuml;"  "Ü"
                        "&szlig;" "ß"
                        "&euro;"  "€"
                        "&amp;"   "&"
                        "&lt;"    "<"
                        "&gt;"    ">"
                        "&copy;"  "©"
                        "&bull;"  "•"
                        "&trade;" "™"
                        "&reg;"   "®"
                        "&sect;"  "§"})
    value))

(defn encode-map-entity [key-map]
  (reduce (fn [r [k v]] (assoc r k (html-char-encode v))) {} key-map))

(defn decode-map-entity [key-map]
  (reduce (fn [r [k v]] (assoc r k (html-char-decode v))) {} key-map))