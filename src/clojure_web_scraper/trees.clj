(ns clojure-tree-tests.core
  (:require
    [clojure.string :as string]
    [clojure-web-scraper.common :as common]
    [clojure.core.async :as async :refer [go go-loop put! take! <! >! <!! timeout chan alt! go]]))

(def some-gen-10-10 [[1 1 0 0 0]
                     [1 0 1 0 0]
                     [1 1 1 0 0]
                     [0 0 0 0 0]
                     [0 0 0 0 0]
                     ])

(def some-other [[1 1 1 0 0]
                 [0 0 1 1 0]
                 [1 0 1 1 0]
                 [1 1 1 0 0]
                 [0 0 0 0 0]
                 ])

;; [[1 1 0 0 0] [1 0 1 0 0] [1 1 1 0 0] [0 0 0 0 0] [0 0 0 0 0]]
;; ((1 1 1 0 0) (0 0 1 1 0) (1 0 1 1 0) (1 1 1 0 0) (0 0 0 0 0))
;; ((0 1 1 1 0) (1 0 0 0 1) (1 0 0 0 1) (1 0 1 1 0) (1 1 1 0 0))

;; ((0 0 0 0 0) (0 0 0 0 0) (0 0 0 0 0) (0 0 0 0 0) (0 0 0 0 0))
;; (next-gen some-other 5 positions)

(defn moore-neighborhood [[x y]]
  (for [dx [-1 0 1]
        dy [-1 0 1]
        :when (not (= [dx dy] [0 0]))]
    [(+ x dx) (+ y dy)]))
(= [1 1] [0 0])
(moore-neighborhood [1 1])


(def up [-1 0])
(def up-right [-1 1])
(def rigth [0 1])
(def down-right [1 1])
(def down [1 0])
(def down-left [1 -1])
(def left [0 -1])
(def left-up [-1 -1])

(def positions [up up-right rigth down-right down down-left left left-up])

(defn neighbours
  "Determines all the neighbours of a given coordinate"
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(mapv + [1 1] [1 1])


(defn is-any-alive-inc [board pos current-pos alive-cell]
  (let [pos-to-check (mapv + pos current-pos)]
    (if (= 1 (get-in board pos-to-check))
      (inc alive-cell)
      alive-cell)))

(defn get-alive-cells[board positions current-pos]
  (loop [positions positions
         alive-cells 0]
    (let [pos (first positions)]
      (if-not pos
        alive-cells
        (recur (rest positions)
               (is-any-alive-inc board pos current-pos alive-cells))))))

(defn is-alive? [num-of-cell]
  (if (or (= num-of-cell 2) (= num-of-cell 3))
    1
    0))

(defn next-gen [a-generation size-matrix positions]
  (vec (for [x (range 0 size-matrix)]
         (vec (for [y (range 0 size-matrix)]
                (is-alive? (get-alive-cells a-generation positions [x y])))))))


(map vector (range 0 5) (range 0 5) )

  (vec (for [x (range 0 5)]
    (vec (for [y (range 0 5)]
      (is-alive? (get-alive-cells some-gen-10-10 positions [x y]))))))

(get-alive-cells some-gen-10-10 positions [0 1])


(next-gen some-other 5 positions)

(defn game-of-life
  ([](game-of-life some-gen-10-10))
  ([some-generation](lazy-seq (cons some-generation (game-of-life (next-gen some-generation 5 positions))))))

(take 300 (game-of-life))

;; Any live cell with fewer than two live neighbours dies, as if caused by under-population.
;; Any live cell with more than three live neighbours dies, as if by over-population.
;; Any live cell with two or three live neighbours lives on to the next generation.
;; Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.

;; ALIVE --> ALIVE  == 2 || == 3
;; ALIVE --> DEAD   < 2  || > 3
;; DEAD  --> ALIVE  == 3

;; LIVE == 2 || == 3
;; DEAD < 2  || > 3
