(ns donut.sugar.utils
  (:require
   [clojure.string :as str]
   [clojure.walk :as walk]
   [clojure.data :as data]

   #?@(:cljs [[goog.string :as gstr]
              [goog.string.format]])))


;;---
;; data structures
;;---

(defn deep-merge-with
  "Like merge-with, but merges maps recursively, applying the given fn
  only when there's a non-map at a particular level.
  (deep-merge-with + {:a {:b {:c 1 :d {:x 1 :y 2}} :e 3} :f 4}
                     {:a {:b {:c 2 :d {:z 9} :z 3} :e 100}})
  -> {:a {:b {:z 3, :c 3, :d {:z 9, :x 1, :y 2}}, :e 103}, :f 4}"
  [f & maps]
  (apply
   (fn m [& maps]
     (if (every? map? maps)
       (apply merge-with m maps)
       (apply f maps)))
   maps))

(defn deep-merge
  "Like merge, but merges maps recursively"
  [db m]
  (deep-merge-with (fn [_ x] x) db m))

(defn update-many-vals
  "Takes a map to be updated, x, and a map of
  {[k1 k2 k3] update-fn-1
   [k4 k5 k6] update-fn-2}
  such that such that k1, k2, k3 are updated using update-fn-1
  and k4, k5, k6 are updated using update-fn-2"
  [x update-map]
  (reduce (fn [x [keys update-fn]]
            (reduce (fn [x k] (update x k update-fn))
                    x
                    keys))
          x
          update-map))

(defn set-toggle
  "Toggle `val`'s membership in set `s`"
  [s val]
  (let [s (or s #{})]
    ((if (contains? s val) disj conj) s val)))

(defn projection?
  "Is every value in x present in y?"
  [x y]
  {:pre [(and (seqable? x) (seqable? y))]}
  (let [diff (second (data/diff y x))]
    (->> (walk/postwalk (fn [x]
                          (when-not (and (map? x)
                                         (nil? (first (vals x))))
                            x))
                        diff)
         (every? nil?))))

(defn dissoc-in
  "Remove value in `m` at `p`"
  [m p]
  (update-in m (butlast p) dissoc (last p)))

(defn move-keys
  "if keys are present at top level, place them in a nested map"
  [m ks path]
  (reduce (fn [m k]
            (if (contains? m k)
              (-> m
                  (assoc-in (conj path k) (get m k))
                  (dissoc k))
              m))
          m
          ks))

(defn key-by
  [k xs]
  (into {} (map (juxt k identity) xs)))

(defn toggle [v x y]
  (if (= v y) x y))

(defn flatv
  [& args]
  (into [] (flatten args)))

(defn vectorize
  [maybe-sequential]
  (if (sequential? maybe-sequential)
    (vec maybe-sequential)
    [maybe-sequential]))

;;---
;; Organize response records for easy frontend consumption
;;---

(defn clj-kvar
  "Given a namespaced keyword, look up the corresponding var in clj compilation or
  return a keyword in cljs compilation."
  [var-name]
  #?(:clj (try @(ns-resolve (symbol (namespace var-name)) (symbol (name var-name)))
               (catch Throwable _t
                 (throw (ex-info (format "could not find var %s" var-name)
                                 {:var-name var-name}))))
     :cljs var-name))

;;---
;; formatting
;;---

(def fmt #?(:clj format :cljs gstr/format))

(defn full-name
  "full string representation of a keyword:
  :x/y => \"x/y\"
  :y => \"y\""
  [k]
  (if (keyword? k)
    (-> k str (subs 1))
    k))

(defn strk
  "Like `str` but with keywords"
  [& xs]
  (->> xs
       (reduce (fn [s x] (str s (name x)))
               "")
       keyword))

(defn slash
  "replace dots with slashes in namespace to create a string that's
  route-friendly"
  [name]
  (str/replace (full-name name) #"\." "/"))

(defn kebab
  "Replace non-alphanumeric characters with hyphens"
  [s]
  (str/replace s #"[^a-zA-Z0-9]" "-"))

(defn slugify
  [txt & [seg-count]]
  (cond->> (-> txt
               str/lower-case
               (str/replace #"-+$" "")
               (str/split #"[^a-zA-Z0-9]+"))
    seg-count (take seg-count)
    true (str/join "-")))

(defn pluralize
  [s n]
  (if (= n 1) s (str s "s")))

(defn capitalize-words
  "Capitalize every word in a string"
  [s]
  (->> (str/split (str s) #"\b")
       (map str/capitalize)
       (str/join)))

(defn kw-title
  "Turn a keyword into a capitalized string"
  [kw]
  (-> (name kw)
      (str/replace #"-" " ")
      capitalize-words))
