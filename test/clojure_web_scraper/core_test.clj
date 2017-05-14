(ns clojure-web-scraper.core-test
  (:require [clojure.test :refer :all]
            [clojure-web-scraper.common :refer :all]))

(deftest get-min-max-test
  (testing "It return numbers"
    (is (= (get-min-max "1-") {:min "1", :max nil}))
    (is (= (get-min-max "1-3") {:min "1", :max "3"}))
    (is (= (get-min-max "1ft") {:min "1", :max nil}))
    (is (= (get-min-max "1-3ft") {:min "1", :max "3"}))
    (is (= (get-min-max "1") {:min "1", :max nil}))
    (is (= (get-min-max "12222") {:min "12222", :max nil}))
    (is (= (get-min-max "flat") {:min "0", :max nil}))
    (is (= (get-min-max "fLaT") {:min "0", :max nil}))
    (is (= (get-min-max "dajskldjsalk") {:min nil :max nil}))))

;; Mon07/11Tue08/11Wed09/11Thu10/11Fri11/11Sat12/11Sun13/11Mon14/11Tue15/11Wed16/11
;; Flat 		1ft 		3-4ft     2-3ft       1-2ft       3-5ft           1-2 ft     1-2 ft       2ft 			1ft
;;   "It is going to split dates: $('#highcharts-0 svg > text').text() Mon07/11Tue08/11Wed09/11Thu10/11Fri11/11Sat12/11Sun13/11Mon14/11Tue15/11Wed16/11"

(def msw-date-test "Mon07/11Tue08/11Wed09/11Thu10/11Fri11/11Sat12/11Sun13/11Mon14/11Tue15/11Wed16/11")

(deftest split-msw-date-test
  (testing "It is returning a list of dates from a string"
    (is (= (split-msw-date-test msw-date-test)))))

(defn splitMSWsurfSize []
  "It is going to split surf size:$('.highcharts-stack-labels text').text() Flat1ft3-4ft2-3ft1-2ft3-5ft1-2ft1-2ft2ft1ft")


(get-min-max-test)
