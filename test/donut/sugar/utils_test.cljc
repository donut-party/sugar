(ns donut.sugar.utils-test
  (:require
   [donut.sugar.utils :as u]
   #?(:clj [clojure.test :as t :refer [is deftest]]
      :cljs [cljs.test :as t :include-macros true :refer [is deftest]])))

;;---
;; data structures
;;---

(deftest deep-merge-with
  (is (= {:a {:b {:z 3, :c 3, :d {:z 9, :x 1, :y 2}}, :e 103}, :f 4}
         (u/deep-merge-with +
                            {:a {:b {:c 1 :d {:x 1 :y 2}} :e 3} :f 4}
                            {:a {:b {:c 2 :d {:z 9} :z 3} :e 100}}))))

(deftest deep-merge
  (is (= {:a {:b {:c :d}} :e :f}
         (u/deep-merge {:a {:b 0} :e :f}
                       {:a {:b {:c :d}}})))

  (is (= {:a {:b 0} :e :f}
         (u/deep-merge {:a {:b {:c :d}}}
                       {:a {:b 0} :e :f})))

  (is (= {:a {:b {:c :d :x :y}} :e :f}
         (u/deep-merge {:a {:b {:c :d}}}
                       {:a {:b {:x :y}} :e :f}))))

(deftest update-many-vals
  (is (= {:a 2, :b 2, :c 2, :d 0, :e 0, :f 0}
         (u/update-many-vals {:a 1, :b 1, :c 1, :d 1, :e 1, :f 1}
                             {[:a :b :c] inc
                              [:d :e :f] dec}))))

(deftest set-toggle
  (is (= (u/set-toggle #{:a :b} :a)
         #{:b})
      (= (u/set-toggle #{:b} :a)
         #{:a :b})))

(deftest projection?
  (is (u/projection? {:a 1}
                     {:a 1 :b 1}))

  (is (not (u/projection? {:a 2}
                          {:a 1 :b 1}))))

(deftest dissoc-in
  (is (= (u/dissoc-in {:a {:b {:c :d}}} [:a :b :c])
         {:a {:b {}}})))

(deftest move-keys
  (is (= {:b   1
          :foo {:bar {:a 1}}}
         (u/move-keys {:a 1 :b 1} [:a] [:foo :bar]))))

(deftest key-by
  (is (= {1 {:id 1 :name :foo}
          2 {:id 2 :name :bar}}
         (u/key-by :id [{:id 1 :name :foo}
                        {:id 2 :name :bar}]))))

(deftest toggle
  (is (= :a (u/toggle :b :a :b)))
  (is (= :b (u/toggle :a :a :b))))

(deftest flatv
  (is (= (u/flatv :forms [:topic :post])
         [:forms :topic :post])))

;;---
;; formatting
;;---

(deftest full-name
  (is (= "x/y"
         (u/full-name :x/y))))

(deftest strk
  (is (= :topic-title
         (u/strk :topic "-title"))))

(deftest kebab
  (is (= "Foo-Bar-0"
         (u/kebab "Foo Bar 0"))))

(deftest slash
  (is (= "foo/bar"
         (u/slash :foo.bar))))

(deftest slugify
  (is (= (u/slugify "a b c d e f g h i j k")
         "a-b-c-d-e-f-g-h-i-j-k"))
  (is (= (u/slugify "a b c d e f g h i j k" 3)
         "a-b-c")))

(deftest pluralize
  (is (= (u/pluralize "cat" 1)
         "cat"))
  (is (= (u/pluralize "cat" 2)
         "cats")))

(deftest capitalize-words
  (is (= "One Two Three"
         (u/capitalize-words "one two three"))))

(deftest kw-title
  (is (= "Topic Title"
         (u/kw-title :topic-title))))

(deftest assoc-many-test
  (is (= {:a {:b 1}
          :c {:d 2}}
         (u/assoc-many {} {[:a :b] 1
                           [:c :d] 2})))

  (is (= {:foo {:a {:b 1}
                :c {:d 2}}}
         (u/assoc-many {}
                       [:foo]
                       {[:a :b] 1
                        [:c :d] 2}))))
