(ns mfp.dijkstra
  (:require [mfp.seed :as seed])
  #?(:cljs (:import [goog.string StringBuffer])))

(def corner "∙")
(def v-wall "│")
(def h-wall "───")
(def body   "   ")

(defn v-wall-p [pred] (if (pred) " "  v-wall))
(defn h-wall-p [pred] (if (pred) "   " h-wall))

(defn draw-top-of-row [sb row]
  (.append sb corner)
  (doseq [cell row]
    (let [north-sigil (h-wall-p #((comp :north :links) cell))]
      (.append sb (str north-sigil corner))))
  (.append sb "\n"))

(defn draw-bot-of-row [sb row]
  (.append sb v-wall)
  (doseq [cell row]
    (let [east-sigil (v-wall-p #((comp :east :links) cell))]
      (.append sb (str body east-sigil))))
  (.append sb "\n"))

(defn print-grid
  [grid]
  (let [sb (StringBuffer.)]
    (doseq [row grid]
      (draw-top-of-row sb row)
      (draw-bot-of-row sb row))
    (let [n-cols (count (first grid))]
      (.append sb (apply str corner
                         (repeat n-cols (str h-wall corner)))))
    (print (str sb "\n"))))

(defn cell-to-north?
  [[row col]]
  (pos? row))

(defn cell-to-east?
  [[row col] n-cols]
  (< col (dec n-cols)))

(defn link-north?
  [n-cols cell toss]
  (or ((complement cell-to-east?) cell n-cols)
      (and (cell-to-north? cell)
           (= :n toss))))

(defn link-east?
  [n-cols cell toss]
  (or ((complement cell-to-north?) cell)
      (and (cell-to-east? cell n-cols)
           (= :e toss))))

(defn link-none?
  [n-cols cell]
  (and ((complement cell-to-north?) cell)
       ((complement cell-to-east?) cell n-cols)))

(defn split-with-from-end
  "`clojure.core/split-with`, but split from the right end of `coll`."
  [f coll]
  (let [[with without] (split-with f (reverse coll))]
    [(vec (reverse without))
     (vec (reverse with))]))

(defn link-run-north
  "Pluck current run of eastward-linked cells out of `coll`, assign a
  north link to a random cell in the run, and graft that new run onto
  the non-participating portion of `coll`."
  [acc]
  (let [[not-run run] (split-with-from-end (comp :east :links) acc)
        run (conj run {:links #{}})
        target (seed/rand-nth (range (count run)))
        new-run (update-in run [target :links] conj :north)]
    (into not-run new-run)))

(defn add-cell
  [acc link-none? link-east? link-north?]
  (let [toss (seed/rand-nth [:n :e])]
    (cond
      (link-none?)       (conj acc {:links #{}})
      (link-east?  toss) (conj acc {:links #{:east}})
      (link-north? toss) (link-run-north acc)
      :else              (conj acc {:links #{}}))))

(def respecting-dynamic-scope doall)

(defn dijkstra-demo
  [n-rows n-cols]
  (let [grid (respecting-dynamic-scope
              (for [row (range n-rows)]
                (loop [acc []
                       col-n 0]
                  (if (= col-n n-cols)
                    acc
                    (let [cell [row col-n]
                          link-none?  (partial link-none?  n-cols cell)
                          link-east?  (partial link-east?  n-cols cell)
                          link-north? (partial link-north? n-cols cell)
                          acc (add-cell acc link-none? link-east? link-north?)]
                      (recur acc (inc col-n)))))))]
    (print-grid grid)))

(defn -main [& args]
  #?(:clj (seed/with-seed (first args)
            (dijkstra-demo 4 4))
     :cljs (do (seed/seed-random! (first args))
               (dijkstra-demo 4 4))))
