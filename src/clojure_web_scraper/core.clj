(ns clojure-web-scraper.core
  (:import
    [com.amazonaws.services.s3 AmazonS3Client]
    [com.amazonaws.services.s3.model Bucket AmazonS3Exception])
  (:gen-class
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  (:require
    [clj-time.format :as f]
    [clj-time.core :as t]
    [clojure.data.json :as json]
    [clojure.string :as string]
    [clojure-web-scraper.common :as common]
    [hickory.core :as hickory]
    [hickory.select :as s]
    [clojure.java.io :as io]
    [clojure.core.async :as async :refer [to-chan buffer pipeline go go-loop put! take! <! >! <!! timeout chan alt! go]]))

(def parallelism (+ (.availableProcessors (Runtime/getRuntime)) 1))

(defn parse-response
  [response]
  (let [{:keys [body error]} response]
    (if error
      false
      body)))

(defn surf-map
  [location source waves tmpe date]
  { :surf-location location
    :source source
    :wave-max waves
    :wave-min waves
    :tmpe tmpe
    :unit "m"
    :date date
    :rating -1})

(defn selector-windguro-get-script-content
  [hickory-tree]
  (->
    (s/select (s/child
                (s/class "obal-wrap")
                (s/tag :script))
      hickory-tree)
    first
    :content
    first
    string/trim))

(defn clean-surf-data
  [raw-data]
  (-> (string/split raw-data #"=")
      second
      json/read-str
      (#(get-in % ["fcst" "3"]))))

(defn read-json
  [json]
  (json/read-str))

(defn create-date-and-return-as-string
  [date d h]
  (let [real-data (t/plus date (t/days d))]
    ; I am doing this to set the h.... after summing the day. this is because
    ; I don't want to sum the h
    (f/unparse common/custom-formatter (t/date-time  (t/year real-data)
                                                     (t/month real-data)
                                                     (t/day real-data)
                                                     h))))

(defn surf-date-shape
  [data]
  (let [{init-date "initdate"
         raw-ds "hr_d"
         raw-hs "hr_h"} data]
    (let [date (f/parse init-date)
          counted-days (common/map-to-integer raw-ds)
          partial-date (partial create-date-and-return-as-string date)]
      (map #(partial-date %1 (common/parse-int %2))
           counted-days
           raw-hs))))

(defn surf-waves-shape
 [data]
 (let [{wave "HTSGW"} data]
   (map #(if (nil? %) 0 %) wave)))

(defn surf-tmpe-shape
  [data]
  (get data "TMPE"))

(def call-urls-pipeline
  (map (fn
         [location]
         (let [surf-location (get location "surfLocation")
               surf-url (get location "url")
               surf-response (parse-response (<!! (common/http-get surf-url)))]
          [surf-location surf-url surf-response]))))

(def transform-response-pipeline
  (map (fn
         [surf-info]
         (let [ [surf-location surf-url surf-raw] surf-info]
           (let [surf-data (-> surf-raw
                               hickory/parse
                               hickory/as-hickory
                               selector-windguro-get-script-content
                               clean-surf-data)]
             (let [h (surf-waves-shape surf-data)
                   t (surf-tmpe-shape surf-data)
                   d (surf-date-shape surf-data)
                   surf-map-with-some-args (partial surf-map surf-location surf-url)]
              (map surf-map-with-some-args h t d)))))))

(defn amazing-pipeline
  [in]
  (let [out-surf-urls (chan 1)
        out-transform-response (chan 1)]
    (pipeline parallelism out-surf-urls call-urls-pipeline in)
    (pipeline parallelism out-transform-response transform-response-pipeline out-surf-urls)
    out-transform-response))

(def s3-client (AmazonS3Client.))

(defn -handleRequest
  [this in out context]
  (let [surf-locations (-> (json/read (io/reader in))
                           first
                           second
                           first
                           (#(get-in % ["Sns" "Message"]))
                           (json/read-str)
                           (to-chan))]
      (let [surf-data (<!! (async/reduce conj [] (amazing-pipeline surf-locations)))]
        (.putObject s3-client "com.surfing" "next/surf.json" (json/write-str surf-data)))))

; test pipeline:
; (time (-handleRequest nil "sns-event.json" "outfile.json" nil))

; (def read-in
;   (-> "raw-data-example.json"
;       io/resource
;       io/file
;       slurp
;       json/read-str
;       (#(get-in % ["fcst" "3"]))))

; aws sns publish \
;     --topic-arn arn:aws:sns:eu-west-1: \
;     --message file://message.json \
;     --subject Ciao

; (get-in (first ciao ["Sns"]))
; (def read-in
;   (-> "locations.json"
;       io/resource
;       io/file
;       slurp
;       json/read-str
;       to-chan))
;
; (surf-date-shape read-in)
; (def d (get-in read-in ["hr_d"]))

; (get-in (first (second (<!! read-in))) ["Sns" "Message"]))))
;
