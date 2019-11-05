(ns mfp.seed
  (:refer-clojure :exclude [rand-nth])
  #?(:clj (:require [clojure.data.generators :as gen]))
  #?(:cljs (:require [cljsjs.seedrandom])))

#?(:clj
   (defmacro with-seed [seed & body]
     `(let [seed# (or ~seed (gen/int))]
        (println "Seed: " seed#)
        (binding [gen/*rnd* (java.util.Random. seed#)]
          ~@body))))

#?(:cljs
   ;; Needs a 10x exclamation point.
   (defn seed-random! [seed]
     (let [seed (let [parsed (js/parseInt seed)]
                  (if-not (js/Number.isNaN parsed)
                    parsed
                    (Math.floor (* (Math.random)
                                   js/Number.MAX_SAFE_INTEGER))))
           prng (Math/seedrandom. seed)]
       (println "Seed: " seed)
       (aset js/Math "random" prng))))

(defn rand-nth [coll]
  (if (empty? coll)
    coll
    #?(:clj (gen/rand-nth coll)
       :cljs (clojure.core/rand-nth coll))))