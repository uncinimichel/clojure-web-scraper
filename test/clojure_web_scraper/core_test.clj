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


(transform-text-number-test)
