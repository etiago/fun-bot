(ns fun-bot.core
  (:require clj-slack.rtm
            clj-slack.users
            [gniazdo.core :as ws]
            [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.tools.logging :as log])
  (:use [clojure.string :only (lower-case includes?)]
        [slingshot.slingshot :only [throw+ try+]])
  (:gen-class))

(def config
  (try+
   (edn/read-string (slurp "resources/config.edn"))
   (catch Object _
     (log/error "Config file (config.edn) not found!")
     (System/exit -1))))

(def msg-id-atom (atom 0))

(defn message-received
  [socket message]
  (if (not (nil? message))
    (let [message-kw (json/read-str message :key-fn keyword)]
      (if (contains? message-kw :text)
          (let [text (lower-case (get-in message-kw [:text]))]
            (if (and (includes? text (get-in config [:bot-name]))
                     (includes? text "tell me a joke"))
              (let [message-id @msg-id-atom]
                (swap! msg-id-atom
                       (fn [current-id]
                         (inc current-id)))
                (ws/send-msg @socket
                             (json/write-str {:id message-id
                                              :type "message"
                                              :channel (get-in message-kw [:channel])
                                              :text "A joke!!"})))))))))

(defn -main
  [& args]
  (let [socket-promise (promise)]
    (future
      (let [socket (ws/connect
                    (get-in (clj-slack.rtm/start
                             {:api-url "https://slack.com/api"
                              :token (get-in config [:api-token])})
                            [:url])
                    :on-receive (partial message-received socket-promise))]
        (deliver socket-promise socket)))))   
