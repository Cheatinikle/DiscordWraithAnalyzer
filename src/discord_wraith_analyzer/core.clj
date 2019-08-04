(ns discord-wraith-analyzer.core
  (:require [clojure.string :as s]
            [discord.bot :as bot]
            [discord.client :as client]
            [discord.http :as http]
            [discord-wraith-analyzer.utils :as util]))

(defn working-command
  [client message]
  (println (:content message))
  (bot/say "https://giphy.com/gifs/9K2nFglCAQClO"))

(defonce user-karma
  (atom {}))

;;; This is the pattern that messages much match to alter the karma in the channel
(defonce karma-message-pattern
  (re-pattern "^<@(?<user>\\d+)>\\s*[+-](?<deltas>[+-]+)\\s*"))

(defn karma-handler [prefix client message]
  (if-let [[match user-id deltas]  (re-find karma-message-pattern (:content message))]
    (let [user-karma-delta  (apply + (for [delta deltas] (case (str delta) "+" 1 "-" -1)))
          current-karma     (get @user-karma user-id 0)
          new-user-karma    (+ current-karma user-karma-delta)]
      (swap! user-karma assoc user-id new-user-karma)
      (bot/say (format "Updating <@%s>'s karma to %s" user-id new-user-karma)))))

(defonce emoji-counts
  (atom {}))

(defn emoji-handler
  [prefix client message]
  (let [emojis (re-seq (re-pattern "<:[^:]+:\\d+>")(:content message))]
    (doseq [emoji emojis]
      (let [new-emoji-count (inc (get @emoji-counts emoji 0))]
        (swap! emoji-counts assoc emoji new-emoji-count)
        (bot/say (format "%s was used %s time(s)" emoji new-emoji-count))))))

(defn initialize-bot
  []
  (reset! bot/extension-registry ()))

(defn start-bot
  [commands handlers]
  (initialize-bot)
  (util/register-commands commands)
  (util/register-handlers handlers)
  (bot/start))

(defn -main
  "I don't do a whole lot."
  [& args]
  (start-bot [{:name "working" :fn working-command }]
             [ karma-handler emoji-handler ]))
