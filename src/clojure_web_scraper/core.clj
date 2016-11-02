(ns clojure-web-scraper.core
  (:require [org.httpkit.client :as http])
  (:require [clojure.data.json :as json])
  (:require [hickory.core :as hickory])
  (:require [hickory.select :as s])
  (:require [clojure.core.async :as async :refer [go go-loop put! take! <! >! <!! timeout chan alt! go]]))


(def surfing-urls [
                   "http://magicseaweed.com/East-Wittering-Surf-Report/14/"
                   "http://magicseaweed.com/West-Wittering-Surf-Report/3765/"
                   "http://magicseaweed.com/Morocco-Surf-Forecast/12/"
                   "http://magicseaweed.com/Central-Morocco-Surfing/42/"
                   "http://magicseaweed.com/Southern-Morocco-Surfing/44/"
                  ])


(defn http-get [url]
  (let [c (chan)]
    (println "Calling this url:" url)
    (http/get url
              (fn [r] (put! c r))
              (fn [err] (println (str "error occured:" err))))
    c))

(defn call-all-the-urls
  "This function is going to call all the urls async and return the results"
  []
  (go
    (time ;; 3520
      (doseq [url surfing-urls]
        (println (<! (http-get url)))))))

(call-all-the-urls)

(defn selector [hickory-tree] (s/select (s/child  (s/class "nomargin")) hickory-tree))

(defn call-all-the-urls-optn
  []
  (go
    (time ;; 2274.652699  msecs
      (let [chans (doall (for [url surfing-url]
                           (http-get url)))]
        (println chans)
        (doseq [c chans]
          (println "Channel value:" (-> c
                                        <!
                                        :body
                                        hickory/parse
                                        selector)))))))

(call-all-the-urls-optn)


(println (<!! (http-get (first surfing-urls))))

(def parsed-doc (hickory/parse "<a href=\"foo\">foo</a>"))
(hickory/as-hiccup parsed-doc)
