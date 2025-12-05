(ns donut.sugar.compose
  (:require [donut.sugar.compose.macros :refer [defcomposer]])
  (:refer-clojure
   :exclude
   [update merge into conj assoc]))

(declare update merge into conj assoc)

(defcomposer update)
(defcomposer merge)
(defcomposer into)
(defcomposer conj)
(defcomposer assoc)

(defn compose-update
  [base path update-val]
  (if-let [composing-function (::composing-function update-val)]
    (update-in base path (fn [x] (apply composing-function x (::args update-val))))
    (assoc-in base path update-val)))

(defn compose
  [base updates]
  (reduce-kv compose-update base updates))
