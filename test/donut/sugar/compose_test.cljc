(ns donut.sugar.compose-test
  (:require [donut.sugar.compose :as sut]
            #?(:clj [clojure.test :refer [deftest is]]
               :cljs [cljs.test :refer [deftest is] :include-macros true])))

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

;; >f versions of functions reverse the first two arguments
(deftest compose-with->f-test
  ;; left side of merge gets merged into right side
  ;; {:b 1} gets merged into {:b 4 :c 2}
  (is (= {:a {:b 1
              :c 2}}
         (sut/compose {:a {:b 1}}
                      {[:a] (sut/>merge {:b 4 :c 2})})))

  (is (= {:foo [4 5 6 1 2 3]}
         (sut/compose {:foo [1 2 3]}
                      {[:foo] (sut/>into [4 5 6])}))))

(deftest map->composable-test
  (is (= {[:a] 1}
         (sut/map->composition {:a 1})))

  (is (= {[:a :b :c] :d}
         (sut/map->composition {:a {:b {:c :d}}})))

  (is (= {[:a :b :c] :d
          [:a :b :e] {}
          [:x]       []
          [:y]       {}}
         (sut/map->composition {:a {:b {:c :d
                                        :e {}}}
                                :x []
                                :y {}}))))

(deftest compose-with-map->composable-test
  (is (= {:a {:b 1
              :c 2}}
         (sut/compose {:a {:b 1}}
                      (sut/map->composition
                       {:a (sut/>merge {:b 4 :c 2})}))))

  (is (= {:foo [4 5 6 1 2 3]}
         (sut/compose {:foo [1 2 3]}
                      (sut/map->composition
                       {:foo (sut/>into [4 5 6])}))))

  (is (= {:a {:b {:c {}}}
          :d {:e [4 5 6 1 2 3]}}
         (sut/compose {:a {:b {:c :going-awway}}
                       :d {:e [1 2 3]}}
                      (sut/map->composition
                       {:a {:b {:c {}}}
                        :d {:e (sut/>into [4 5 6])}})))))
