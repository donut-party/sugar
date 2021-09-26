(ns donut.sugar.routes-load-handlers
  "Contains no tests. Exists to test that donut.sugar.routes/load-handlers! works"
  )

(def handlers
  {:collection    {:get {:handler :foo}}
   :member        {:post {:handler :bar}}
   :member/action {:get {:handler :bux}}})
