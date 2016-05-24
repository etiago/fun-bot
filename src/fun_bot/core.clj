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

(defn- send-message-to-slack
  "Given a socket and an atom, send a text message to a Slack channel"
  [text socket msg-id-atom channel]
  (if (not (nil? text))
    (let [message-id @msg-id-atom]
      (swap! msg-id-atom
             (fn [current-id]
               (inc current-id)))
      (ws/send-msg socket
                   (json/write-str {:id message-id
                                    :type "message"
                                    :channel channel
                                    :text text})))))

(defn- message-received
  "Callback function for websockets' client"
  [socket-promise msg-id-atom trigger-cb-vector message]
  (if (not (nil? message))
    (let [message-kw (json/read-str message :key-fn keyword)]
      (if (and
           (contains? message-kw :type)
           (= "message"
              (get-in message-kw [:type]))
           (contains? message-kw :text))
        (let [text (lower-case (get-in message-kw [:text]))]
          (doseq [trigger-cb trigger-cb-vector]
            (send-message-to-slack
             (trigger-cb text)
             @socket-promise
             msg-id-atom
             (get-in message-kw [:channel]))))))))

;; API
(defn start
  "Takes a Slack API token and a vector of functions.
  The functions take one argument (the message being posted
  in the channel) and should return a reaction in the form
  of a string, or nil if no reaction is to be returned."
  [api-token trigger-cb-vector]
  (let [socket-promise (promise)
        msg-id-atom (atom 0)]
    (future
      (let [socket (ws/connect
                    (get-in (clj-slack.rtm/start
                             {:api-url "https://slack.com/api"
                              :token api-token})
                            [:url]) ;; Start the RTM session and get a websocket URL to connect to
                    :on-receive (partial message-received
                                         socket-promise
                                         msg-id-atom
                                         trigger-cb-vector))]
        (deliver socket-promise socket)))))
