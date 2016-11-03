(ns clojure-web-scraper.core
  (:require
    [clojure-web-scraper.common :as common]
    [hickory.core :as hickory]
    [hickory.select :as s]
    [clojure.core.async :as async :refer [go go-loop put! take! <! >! <!! timeout chan alt! go]]))

(def surfing-urls [
                    { :key "East-Wittering" :url "http://magicseaweed.com/East-Wittering-Surf-Report/14/"}
                    { :key "West-Wittering" :url "http://magicseaweed.com/West-Wittering-Surf-Report/3765/"}
                    { :key "Tamri" :url "http://magicseaweed.com/Tamri-Surf-Report/1086/"}
                    { :key "Haouzia" :url "http://magicseaweed.com/Haouzia-Surf-Report/3118/"}
                    ])

(defn selector-get-waves-size-text [hickory-tree] (-> (s/select (s/class "rating-text") hickory-tree)
                                                      first :content first string/trim))



(defn call-all-the-urls-optn
  []
  (go
    (let [urls-channels (doall (for [pair surfing-urls]
                                 [(:key pair) (common/http-get (:url pair))]))]
      (doseq [[url-key c] urls-channels]
        (println "Surfing for: " url-key (-> c
                                         <!
                                         :body
                                         hickory/parse
                                         hickory/as-hickory
                                         selector-get-waves-size-text
                                         common/transform-text-number))))))

(call-all-the-urls-optn)
