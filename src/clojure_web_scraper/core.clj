(ns clojure-web-scraper.core
  (:require [org.httpkit.client :as http])
  (:require [clojure.data.json :as json])
  (:require [clojure.string :as string])
  (:require [hickory.core :as hickory])
  (:require [hickory.select :as s])
  (:require [clojure.core.async :as async :refer [go go-loop put! take! <! >! <!! timeout chan alt! go]]))


(def surfing-urls [
                    "http://magicseaweed.com/East-Wittering-Surf-Report/14/"
                    "http://magicseaweed.com/West-Wittering-Surf-Report/3765/"
                    "http://magicseaweed.com/Tamri-Surf-Report/1086/"
                    "http://magicseaweed.com/Haouzia-Surf-Report/3118/"
                    ])


(defn http-get [url]
  (let [c (chan)]
    (println "Calling this url:" url)
    (http/get url
              (fn [r] (put! c r))
              (fn [err] (println (str "error occured:" err))))
    c))

(defn selector-get-waves-size-text [hickory-tree] (-> (s/select (s/class "rating-text") hickory-tree)
                                                 first :content first string/trim))

(defn transform-text-number
  [text]
    (if (= (string/lower-case text) "flat")
      (int 0)
      (if (integer? text)
        (int text)
        (second (re-matches #"\d+-(\d+)" text)))))

(if (number? "1") "1" nil)
(transform-text-number "1")
(re-matches #"\d+-(\d+)" "1")

(integer? "12---2")

(defn call-all-the-urls-optn
  []
  (go
    (let [urls-channels (doall (for [url surfing-urls]
                                 [url (http-get url)]))]
      (doseq [url-channel urls-channels]
        (println "Channel value:" "for url:" (first url-channel) (-> (second url-channel)
                                                                     <!
                                                                     :body
                                                                     hickory/parse
                                                                     hickory/as-hickory
                                                                     selector-get-waves-size-text
                                                                     transform-text-number))))))

(call-all-the-urls-optn)


(println (<!! (http-get (first surfing-urls))))

(def parsed-doc (hickory/parse "<a href=\"foo\">foo</a>"))
(hickory/as-hiccup parsed-doc)


;; (defn call-all-the-urls
;;   "This function is going to call all the urls async and return the results"
;;   []
;;   (go
;;     (time ;; 3520
;;       (doseq [url surfing-urls]
;;         (println (<! (http-get url)))))))

;; (call-all-the-urls)
