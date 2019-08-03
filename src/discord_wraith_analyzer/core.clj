(ns discord-wraith-analyzer.core
  (:require [clojure.string :as s]
            [discord.bot :as bot]))

(defn- working-command
  [client message]
  (println (:content message))
  (bot/say "https://giphy.com/gifs/9K2nFglCAQClO"))

(bot/defcommand working
  [client message]
  "Posts the Star Wars Episode 1 'It's working' gif in the channel"
  (working-command client message))

(defonce user-karma
  (atom {}))

;;; This is the pattern that messages much match to alter the karma in the channel
(defonce karma-message-pattern
  (re-pattern "^<@(?<user>\\d+)>\\s*[+-](?<deltas>[+-]+)\\s*"))

(bot/defextension karma [client message]
  (:get
    (let [users  (map :id (:user-mentions message))
          karmas (for [user users] (format "<@%s>: %s" user (get @user-karma user 0)))]
      (bot/say (format "Karma: \n%s" (s/join \newline karmas)))))

  (:clear
    "Clear the karma of all users in the channel."
    (reset! user-karma {})))

(bot/defhandler karma-message-handler [prefix client message]
  (if-let [[match user-id deltas]  (re-find karma-message-pattern (:content message))]
    (let [user-karma-delta  (apply + (for [delta deltas] (case (str delta) "+" 1 "-" -1)))
          current-karma     (get @user-karma user-id 0)
          new-user-karma    (+ current-karma user-karma-delta)]
      (swap! user-karma assoc user-id new-user-karma)
      (bot/say (format "Updating <@%s>'s karma to %s" user-id new-user-karma)))))

(defonce emoti-counts
  (atom {}))

(defn- emoti-handler
  [prefix client message]
  (when-let [emotis (re-seq (re-pattern ":[^:]+:")(:content message))]
    (for [emoti emotis]
      (let [new-emoti-count (inc (get @emoti-counts emoti 0))]
        (println new-emoti-count emoti)
        (swap! emoti-counts assoc emoti new-emoti-count)
        (bot/say (format "%s was used %s time(s)" emoti new-emoti-count))))))

(bot/defhandler emoti-message-handler
  [prefix client message]
  (emoti-handler prefix client message))
  
(defn -main
  "I don't do a whole lot."
  [& args]
  (bot/start))
