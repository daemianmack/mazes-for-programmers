(ns mfp.binary-tree)

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

(defn diag-print
  ([grid]
   (diag-print grid nil))
  ([grid current-cell]
   (let [n-cols (count (first grid))
         v-wall "│"
         corner "∙"]
     (doseq [row grid]
       (println
        (apply str corner
               (for [cell row]
                 (let [north-sigil (if (= :north (:link cell))
                                     "   "
                                     "───")]
                   (apply str north-sigil corner)))))
       (println
        (apply str
               v-wall
               (for [cell row]
                 (let [body (if (= current-cell cell)
                              " x "
                              "   ")
                       east-sigil (if (= :east (:link cell))
                                    " "
                                    v-wall)]
                   (apply str body east-sigil))))))
     (println (apply str corner (repeat n-cols (str "───" corner)))))))

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

