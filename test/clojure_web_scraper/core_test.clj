(ns clojure-web-scraper.core-test
  (:require [clojure.test :refer :all]
            [clojure-web-scraper.common :refer :all]))

(deftest transform-text-number-test
  (testing "It return numbers"
    (is (= (transform-text-number "1-") 0))
    (is (= (transform-text-number "1") 1))
    (is (= (transform-text-number "12222") 12222))
    (is (= (transform-text-number "flat") 0))
    (is (= (transform-text-number "fLaT") 0))
    (is (= (transform-text-number "dajskldjsalk") 0))))

;; Mon07/11Tue08/11Wed09/11Thu10/11Fri11/11Sat12/11Sun13/11Mon14/11Tue15/11Wed16/11
;; Flat 		1ft 		3-4ft     2-3ft       1-2ft       3-5ft           1-2 ft     1-2 ft       2ft 			1ft
;;   "It is going to split dates: $('#highcharts-0 svg > text').text() Mon07/11Tue08/11Wed09/11Thu10/11Fri11/11Sat12/11Sun13/11Mon14/11Tue15/11Wed16/11"

(def msw-date-test "Mon07/11Tue08/11Wed09/11Thu10/11Fri11/11Sat12/11Sun13/11Mon14/11Tue15/11Wed16/11")

(deftest split-msw-date-test
  (testing "It is returning a list of dates from a string"
    (is (= (split-msw-date-test msw-date-test )))))

(defn splitMSWsurfSize []
  "It is going to split surf size:$('.highcharts-stack-labels text').text() Flat1ft3-4ft2-3ft1-2ft3-5ft1-2ft1-2ft2ft1ft")



(transform-text-number-test)
