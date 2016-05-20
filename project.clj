(defproject fun-bot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.julienxx/clj-slack "0.5.4"]
                 [stylefruits/gniazdo "1.0.0"]
                 [org.clojure/data.json "0.2.6"]
                 [slingshot "0.12.2"]]
  :main ^:skip-aot fun-bot.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
