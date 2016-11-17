(ns clojure-web-scraper.common
  (:import (javax.crypto Mac)
           (javax.crypto.spec SecretKeySpec))
  (:require [clojure.string :as string]
            [org.httpkit.client :as http]
            [clojure-web-scraper.common :as common]
            [clojure.core.async :as async :refer [go go-loop put! take! <! >! <!! timeout chan alt! go]]))



(defn find-text-error [text] (if (= (string/lower-case text) "error") (int -1) false))
(defn find-text-flat  [text] (if (= (string/lower-case text) "flat")  (int 0) false))
(defn find-one-number
  [text]
  (let [match (re-matches  #"\d+" text)]
    (if match (Integer. match) false)))

(defn find-range-number-return-latest
  [text]
  (let [match (re-matches #"\d+-(\d+)" text)]
    (if match (Integer. (second match)) false)))
(def find-checks [find-text-error find-text-flat find-one-number find-range-number-return-latest])


(defn transform-text-number
  [text]
  (loop [checks find-checks]
    (let [check (first checks)]
      (if (nil? check)
        (int -1)
        (if-let [result (check text)]
          result
          (recur (rest checks)))))))


(defn http-get [url]
  (let [c (chan)]
    (println "Calling this url:" url)
    (http/get url (fn [r] (put! c r)))
    c))

