(ns mfp.binary-tree
  (:require [mfp.seed :as seed])
  #?(:cljs (:import [goog.string StringBuffer])))

(defn cell-to-north?
  [[row col]]
  (pos? row))

(defn cell-to-east?
  [[row col] n-cols]
  (< col (dec n-cols)))

(defn neighbors
  [cell n-cols]
  (cond-> []
    (cell-to-north? cell)        (conj :north)
    (cell-to-east?  cell n-cols) (conj :east)))

(def corner "∙")
(def v-wall "│")
(def h-wall "───")
(def body   "   ")
(defn v-wall-p [pred] (if (pred) " " v-wall))
(defn h-wall-p [pred] (if (pred) "   " h-wall))

(defn draw-top-of-row [sb row]
  (.append sb corner)
  (doseq [cell row]
    (let [north-sigil (h-wall-p #(= :north (:link cell)))]
      (.append sb (str north-sigil corner))))
  (.append sb "\n"))

(defn draw-bot-of-row [sb row]
  (.append sb v-wall)
  (doseq [cell row]
    (let [east-sigil (v-wall-p #(= :east (:link cell)))]
      (.append sb (str body east-sigil))))
  (.append sb "\n"))

(defn print-grid
  [grid]
  (doseq [row grid
          :let [sb (StringBuffer.)]]
    (draw-top-of-row sb row)
    (draw-bot-of-row sb row)
    (print (str sb)))
  (let [n-cols (count (first grid))]
    (println (apply str corner
                    (repeat n-cols (str h-wall corner))))))

(def respecting-dynamic-scope doall)

(defn binary-tree-demo
  [n-rows n-cols]
  (let [grid (respecting-dynamic-scope
              (for [row (range n-rows)]
                (for [col (range n-cols)]
                  (let [cell [row col]
                        neighbor (seed/rand-nth (neighbors cell n-cols))]
                    {:link neighbor}))))]
    (print-grid grid)))

(defn -main [& args]
  #?(:clj (seed/with-seed (first args)
            (binary-tree-demo 4 4))
     :cljs (do (seed/seed-random! (first args))
               (binary-tree-demo 4 4))))

