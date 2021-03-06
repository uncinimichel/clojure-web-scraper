(ns clojure-web-scraper.common
  (:require [clojure.string :as string]
            [clj-time.format :as f]
            [org.httpkit.client :as http]
            [clojure.core.async :as async :refer [go go-loop put! take! <! >! <!! timeout chan alt! go]]))


(def custom-formatter (f/formatter "yyyy-MM-dd HH:mm"))

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s)))

(defn http-get [url]
  (let [c (chan)]
    (println "Calling this url:" url)
    (http/get url (fn [r] (put! c r)))
    c))

(defn map-to-integer [col]
  (loop [acc []
         c col
         old-value nil
         current-index -1]
    (if (empty? c)
      acc
      (let [v (first c)]
        (if (= v old-value)
          (recur (conj acc current-index)
                 (rest c)
                 v
                 current-index)
          (recur (conj acc (inc current-index))
                 (rest c)
                 v
                 (inc current-index)))))))
