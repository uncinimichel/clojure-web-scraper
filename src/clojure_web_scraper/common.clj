(ns clojure-web-scraper.common

  (:import (javax.crypto Mac)
           (javax.crypto.spec SecretKeySpec))
  (:require [clojure.string :as string]
            [org.httpkit.client :as http]
            [clojure-web-scraper.common :as common]
            [clojure.core.async :as async :refer [go go-loop put! take! <! >! <!! timeout chan alt! go]]))


(defn transform-text-number
  [text]

  (if (= (string/lower-case text) "error")
    (int -1)
    (if (= (string/lower-case text) "flat")
      (int 0)
      (if (re-matches  #"\d+" text)
        (Integer. text)
        (if (re-matches #"\d+-(\d+)" text)
          (Integer. (second (re-matches #"\d+-(\d+)" text)))
          (int 0))))))


;; (defn hmac
;;   "Calculate HMAC signature for given data."
;;   [^String key ^String data]
;;   (let [hmac-sha1 "HmacSHA256"
;;         signing-key (SecretKeySpec. (.getBytes key) hmac-sha1)
;;         mac (doto (Mac/getInstance hmac-sha1) (.init signing-key))]
;;     (String. (org.apache.commons.codec.binary.Base64/encodeBase64
;;               (.doFinal mac (.getBytes data)))
;;              "UTF-8")))


;; (hmac "ciao" "123")

;; (http/get "dsadass" (fn [v] (println v))(fn [v] (println "daskljdasksda" v)))


(defn http-get [url]
  (let [c (chan)]
    (println "Calling this url:" url)
    (http/get url (fn [r] (put! c r)))
    c))

