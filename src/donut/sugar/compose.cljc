(ns donut.sugar.compose
  (:require
   [donut.sugar.compose.macros :refer [defcomposer]])
  (:refer-clojure
   :exclude
   [update merge into conj assoc map mapv]))

(declare
 update  merge  into  conj  assoc  map  mapv
 >update >merge >into >conj >assoc >map >mapv >f)

(defcomposer update)
(defcomposer merge)
(defcomposer into)
(defcomposer conj)
(defcomposer assoc)
(defcomposer map)
(defcomposer mapv)

(defn >f
  [f]
  (fn arg-swapped
    ([a b] (f b a))
    ([a b & rest] (apply f b a rest))))

(defn compose-update
  [base path update-val]
  (if-let [composing-function (::composing-function update-val)]
    (update-in base path (fn [x] (apply composing-function x (::args update-val))))
    (assoc-in base path update-val)))

(defn compose
  [base composition]
  (reduce-kv compose-update base composition))

(defn map->composition
  [m]
  (loop [composable      {}
         remaining-paths (clojure.core/mapv vector (keys m))]
    (let [[current-path & new-remaining-paths] remaining-paths
          current-value                        (get-in m current-path)
          current-map?                         (map? current-value)]
      (cond
        (empty? remaining-paths)
        composable

        (or (not current-map?)
            (and current-map?
                 (or (empty? current-value)
                     (::composing-function current-value))))
        (recur (clojure.core/assoc composable current-path current-value)
               new-remaining-paths)

        :else
        (recur composable
               (clojure.core/into
                (clojure.core/mapv (fn [k] (clojure.core/conj current-path k))
                                   (keys current-value))
                new-remaining-paths))))))

;; TODO composer for or
;; TODO rename composer to updater?
