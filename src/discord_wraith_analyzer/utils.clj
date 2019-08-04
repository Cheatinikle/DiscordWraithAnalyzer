(ns discord-wraith-analyzer.utils
  (:require [clojure.string :as s]
            [discord.bot :as bot]
            [taoensso.timbre :as timbre]))

; Temporary feature.
(defn register-commands
  [commands]
  (doseq [command commands]
    (let [name (:name command)
          command-fn (:fn command)]
      (timbre/infof "Registering custom command : %s" name)
      (bot/register-extension! name command-fn))))

(defn register-handlers
  [handlers]
  (doseq [handler-fn handlers]
    (timbre/infof "Registering custom message handler: %s" handler-fn)
    (bot/add-handler! handler-fn)))
