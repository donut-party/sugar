(ns donut.sugar.compose-test
  (:require [donut.sugar.compose :as sut]
            #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer :all :include-macros true])))

(deftest compose-test
  (is (= {}
         (sut/compose {} {})))

  (is (= {:foo :bar}
         (sut/compose {} {[:foo] :bar})))

  (is (= {:foo [:bar :baz]}
         (sut/compose {:foo [:bar]}
                      {[:foo] (sut/conj :baz)})))

  (is (= {:a {:b {:c '(3 2 1)}}}
         (sut/compose {:a nil}
                      {[:a :b :c] (sut/into [1 2 3])})))

  (is (= {:a {:b 1
              :c 2
              :d 3}}
         (sut/compose {:a {:b 1}}
                      {[:a] (sut/merge {:c 2 :d 3})}))))
