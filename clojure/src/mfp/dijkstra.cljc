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
  (.append sb v-wall)
  (doseq [cell row]
    (let [east-sigil (v-wall-p #((comp :east :links) cell))]
      (.append sb (str body east-sigil))))
  (.append sb "\n"))

(defn draw-bot-of-row [sb row]
  (.append sb corner)
  (doseq [cell row]
    (let [south-sigil (h-wall-p #((comp :south :links) cell))]
      (.append sb (str south-sigil corner))))
  (.append sb "\n"))

(defn print-grid
  [grid]
  (let [sb (StringBuffer.)]
    (let [n-cols (count (first grid))]
      (.append sb (apply str corner
                         (repeat n-cols (str h-wall corner))))
      (.append sb "\n"))
    (doseq [row grid]
      (draw-top-of-row sb row)
      (draw-bot-of-row sb row))
    (print (str sb "\n"))))

(defn cell-to-south?
  "Is cell's row not the bottom row?"
  [{x :x :as cell} n-rows]
  (< x (dec n-rows)))

(defn cell-to-east?
  "Is cell's column not the last column?"
  [{y :y :as cell} n-cols]
  (< y (dec n-cols)))

(defn link-south?
  [n-rows n-cols cell toss]
  (or ((complement cell-to-east?) cell n-cols)
      (and (cell-to-south? cell n-rows)
           (= :s toss))))

(defn link-east?
  [n-rows n-cols cell toss]
  (or ((complement cell-to-south?) cell n-rows)
      (and (cell-to-east? cell n-cols)
           (= :e toss))))

(defn link-none?
  [n-rows n-cols cell]
  (and ((complement cell-to-south?) cell n-rows)
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
  [acc cell]
  (let [[not-run run] (split-with-from-end (comp :east :links) acc)
        run (conj run (assoc cell :links #{}))
        target (seed/rand-nth (range (count run)))
        new-run (update-in run [target :links] conj :south)]
    (into not-run new-run)))

(defn add-cell
  [acc cell link-none? link-east? link-south?]
  (let [toss (seed/rand-nth [:s :e])]
    (cond
      (link-none?)       (conj acc (assoc cell :links #{}))
      (link-east?  toss) (conj acc (assoc cell :links #{:east}))
      (link-south? toss) (link-run-north acc cell)
      :else              (conj acc (assoc cell :links #{})))))

(def respecting-dynamic-scope doall)

(defn dijkstra-demo
  [n-rows n-cols]
  (let [grid (respecting-dynamic-scope
              (for [row (range n-rows)]
                (loop [acc []
                       col-n 0]
                  (if (= col-n n-cols)
                    acc
                    (let [cell {:x row :y col-n}
                          link-none?  (partial link-none?  n-rows n-cols cell)
                          link-east?  (partial link-east?  n-rows n-cols cell)
                          link-south? (partial link-south? n-rows n-cols cell)
                          acc (add-cell acc cell link-none? link-east? link-south?)]
                      (recur acc (inc col-n)))))))]
    (print-grid grid)))

(defn -main [& args]
  #?(:clj (seed/with-seed (first args)
            (dijkstra-demo 4 4))
     :cljs (do (seed/seed-random! (first args))
               (dijkstra-demo 4 4))))
