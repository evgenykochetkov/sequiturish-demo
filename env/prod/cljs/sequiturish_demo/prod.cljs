(ns sequiturish-demo.prod
  (:require [sequiturish-demo.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
