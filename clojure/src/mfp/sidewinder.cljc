(ns mfp.sidewinder
  (:require [mfp.seed :as seed])
  #?(:cljs (:import [goog.string StringBuffer])))

(def corner "∙")
(def v-wall "│")
(def h-wall "───")
(def body   "   ")
(defn v-wall-p [pred] (if (pred) " " v-wall))
(defn h-wall-p [pred] (if (pred) "   " h-wall))
(defn body-p   [pred] (if (pred) body body))

(defn draw-top-of-row [sb row]
  (.append sb corner)
  (doseq [cell row]
    (let [north-sigil (h-wall-p #((:links cell) :north))]
      (.append sb (str north-sigil corner))))
  (.append sb "\n"))

(defn draw-bot-of-row [sb row current-cell]
  (.append sb v-wall)
  (doseq [cell row]
    (let [body-sigil "   "
          east-sigil (v-wall-p #((:links cell) :east))]
      (.append sb (str body-sigil east-sigil))))
  (.append sb "\n"))

(defn diag-print
  ([grid]
   (diag-print grid nil))
  ([grid current-cell]
   (doseq [row grid
           :let [sb (StringBuffer.)]]
     (draw-top-of-row sb row)
     (draw-bot-of-row sb row current-cell)
     (print (str sb)))
   (let [n-cols (count (first grid))]
     (println (apply str corner
                     (repeat n-cols (str h-wall corner)))))))

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

(defn reverse-split-with
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
  (let [[not-run run] (reverse-split-with (comp :east :links) acc)
        run (conj run {:links #{}})
        target (seed/rand-nth (range (count run)))
        new-run (update-in run [target :links] conj :north)]
    (into not-run new-run)))

(defn make-acc
  [acc link-none? link-east? link-north?]
  (let [toss (seed/rand-nth [:n :e])]
    (cond
      (link-none?)       (conj acc {:links #{}})
      (link-east?  toss) (conj acc {:links #{:east}})
      (link-north? toss) (link-run-north acc)
      :else              (conj acc {:links #{}}))))

(def respecting-dynamic-scope doall)

(defn sidewinder-demo
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
                          acc (make-acc acc link-none? link-east? link-north?)]
                      (recur acc (inc col-n)))))))]
    (diag-print grid)))

(defn -main [& args]
  #?(:clj (seed/with-seed (first args)
            (sidewinder-demo 4 4))
     :cljs (do (seed/seed-random! (first args))
               (sidewinder-demo 4 4))))