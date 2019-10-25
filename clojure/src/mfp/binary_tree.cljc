(ns mfp.binary-tree
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
(def body   " ✖ ")
(defn v-wall-p [pred] (if (pred) " " v-wall))
(defn h-wall-p [pred] (if (pred) "   " h-wall))
(defn body-p   [pred] (if (pred) body "   "))

(defn draw-top-of-row [sb row]
  (.append sb corner)
  (doseq [cell row]
    (let [north-sigil (h-wall-p #(= :north (:link cell)))]
      (.append sb (str north-sigil corner))))
  (.append sb "\n"))

(defn draw-bot-of-row [sb row current-cell]
  (.append sb v-wall)
  (doseq [cell row]
    (let [body-sigil (body-p #(= current-cell cell))
          east-sigil (v-wall-p #(= :east (:link cell)))]
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

(defn binary-tree-demo
  [n-rows n-cols]
  (let [grid (for [row (range n-rows)]
               (for [col (range n-cols)]
                 (let [cell [row col]
                       neighbor (-> (neighbors cell n-cols)
                                    shuffle
                                    first)]
                   {:link neighbor})))]
    (diag-print grid)))

(defn -main []
  (binary-tree-demo 4 4))

