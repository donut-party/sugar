(ns donut.sugar.compose.macros
  #?(:cljs (:require-macros [donut.sugar.compose.macros :refer [defcomposer]])))

#?(:clj
   (defmacro defcomposer
     "creates a composing function over "
     [fname]
     `(defn ~fname
        [& args#]
        {:donut.sugar.compose/composing-function ~(symbol "clojure.core" (name fname))
         :donut.sugar.compose/args               args#})))
