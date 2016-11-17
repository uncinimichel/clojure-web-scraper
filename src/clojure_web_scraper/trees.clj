(ns clojure-tree-tests.core
  (:require
    [clojure.string :as string]
    [clojure-web-scraper.common :as common]
    [clojure.core.async :as async :refer [go go-loop put! take! <! >! <!! timeout chan alt! go]]))

(def some-gen-10-10 [[0 0 0 0 0]
                     [0 0 0 0 0]
                     [0 0 0 0 0]
                     [0 0 0 0 0]
                     [0 0 0 0 0]
                     ])


(def up [0 -1])
(def up-right [1 -1])
(def rigth [1 0])
(def down-right [1 1])
(def down [0 1])
(def down-left [-1 1])
(def left [-1 0])
(def left-up [-1 -1])

(def positions [up up-right rigth down-right down down-left left left-up])

(defn get-alive-cells[board positions]
  (loop [positions positions
         alive-cells 0]
    (let [pos (first positions)]
      (if-not pos
        alive-cells
        (recur (rest positions)
               (inc alive-cells))))))

(get-alive-cells some-gen-10-10 positions)

(get-element-by-position [-10 1])

(get-in some-gen-10-10 [1 1])
