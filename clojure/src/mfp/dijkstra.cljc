(ns mfp.dijkstra
  (:require [mfp.seed :as seed])
  #?(:cljs (:import [goog.string StringBuffer])))

(def corner "∙")
(def v-wall "│")
(def h-wall "───")

(defn v-wall-p [pred] (if (pred) " "  v-wall))
(defn h-wall-p [pred] (if (pred) "   " h-wall))

(defn body [{cost :cost}]
  (str " "
       #?(:clj  (Integer/toString cost 36)
          :cljs (.toString cost 36))
       " "))

(defn draw-top-of-row [sb row]
  (.append sb v-wall)
  (doseq [cell row]
    (let [east-sigil (v-wall-p #((comp :east :links) cell))]
      (.append sb (str (body cell) east-sigil))))
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

(defn link-run-south
  "Pluck current run of eastward-linked cells out of `coll`, assign a
  south link to a random cell in the run, and graft that new run onto
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
      (link-south? toss) (link-run-south acc cell)
      :else              (conj acc (assoc cell :links #{})))))

(defmulti find-neighbor (fn [cell indexed link] link))

(defmethod find-neighbor :east
  [{x :x y :y} indexed link]
  (let [east-key {:x x :y (inc y)}]
    (get indexed east-key)))

(defmethod find-neighbor :south
  [{x :x y :y} indexed link]
  (let [south-key {:x (inc x) :y y}]
    (get indexed south-key)))

(defmethod find-neighbor :west
  [{x :x y :y} indexed link]
  (let [west-key {:x x :y (dec y)}]
    (get indexed west-key)))

(defmethod find-neighbor :north
  [{x :x y :y} indexed link]
  (let [north-key {:x (dec x) :y y}]
    (get indexed north-key)))

(defn find-costs
  [root indexed]
  (loop [costs {root {:cost 0}}
         frontier [root]
         visited #{}]
    (let [frontier (remove visited frontier)]
      (if (empty? frontier)
        costs
        (let [cell (first frontier)
              new-frontier (->> (:links cell)
                                (mapcat (partial find-neighbor cell indexed))
                                (remove visited))
              costs (reduce (fn [acc new-cell]
                              (assoc-in acc [new-cell :cost]
                                        (inc (get-in costs [cell :cost]))))
                            costs
                            new-frontier)]
          (recur costs
                 (into (vec (rest frontier)) new-frontier)
                 (conj visited cell)))))))

(defn add-costs
  [grid]
  (let [stream (mapcat identity grid)
        indexed (group-by #(select-keys % [:x :y]) stream)
        with-costs (find-costs (ffirst grid) indexed)]
    (reduce (fn [acc [{x :x y :y} {cost :cost}]]
              (assoc-in acc [x y :cost] cost))
            grid
            with-costs)))

(defmulti reciprocal-link-desc (fn [cell link] link))

(defmethod reciprocal-link-desc :east
  [{x :x y :y} _]
  {:x x :y (inc y) :link-to #{:west}})

(defmethod reciprocal-link-desc :south
  [{x :x y :y} _]
  {:x (inc x) :y y :link-to #{:north}})

(defn add-reciprocal-links
  [grid]
  (let [link-descs (for [row grid
                         cell row
                         link (:links cell)]
                     (reciprocal-link-desc cell link))]
    (reduce
     (fn [acc {:keys [x y link-to]}]
       (update-in acc [x y :links] into link-to))
     grid
     link-descs)))

(defn dijkstra-demo
  [n-rows n-cols]
  (let [grid (for [row (range n-rows)]
               (loop [cells []
                      col 0]
                 (if (= col n-cols)
                   cells
                   (let [cell {:x row :y col}
                         link-none?  (partial link-none?  n-rows n-cols cell)
                         link-east?  (partial link-east?  n-rows n-cols cell)
                         link-south? (partial link-south? n-rows n-cols cell)
                         cells (add-cell cells cell link-none? link-east? link-south?)]
                     (recur cells (inc col))))))]
    (-> (vec grid)
        add-reciprocal-links
        add-costs
        print-grid)))

(defn -main [& args]
  #?(:clj (seed/with-seed (first args)
            (dijkstra-demo 4 4))
     :cljs (do (seed/seed-random! (first args))
               (dijkstra-demo 4 4))))
