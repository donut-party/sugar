(ns donut.sugar.compose.macros
  #?(:cljs (:require-macros [donut.sugar.compose.macros :refer [defcomposer]])))

#?(:clj
   (defmacro defcomposer
     "creates a composing function over "
     [fname]
     `(do
        (defn ~fname
          [& args#]
          {:donut.sugar.compose/composing-function ~(symbol "clojure.core" (name fname))
           :donut.sugar.compose/args               args#})
        (defn ~(symbol (str ">" fname))
          [& args#]
          {:donut.sugar.compose/composing-function (donut.sugar.compose/>f ~(symbol "clojure.core" (name fname)))
           :donut.sugar.compose/args               args#}))))
