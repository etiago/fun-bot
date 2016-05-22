(defproject org.tiago/fun-bot "1.0.0"
  :description "fun-bot allows you to create a Slack bot which replies to the hooks you define"
  :url "https://github.com/etiago/fun-bot"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.julienxx/clj-slack "0.5.4"]
                 [stylefruits/gniazdo "1.0.0"]
                 [org.clojure/data.json "0.2.6"]
                 [slingshot "0.12.2"]]
  :main ^:skip-aot fun-bot.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
