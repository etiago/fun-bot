(ns example-fun-bot.core
  (:require [fun-bot.core :as fb])
  (:gen-class))

(defn -main
  [& args]
  (fb/start
   "YOUR_SLACK_API_TOKEN"
   [(fn [text]
      (if (= "bla" text)
        "foobar"))
    (fn [text]
      (if (= "boo" text)
        "foo"))]
   ))
