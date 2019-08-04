(ns discord-wraith-analyzer.core-test
  (:require [clojure.test :refer :all]
            [discord-wraith-analyzer.core :refer :all]
            [taoensso.timbre :as timbre]))

(deftest emoji-test
  (testing "Can it detect emojis?"
    (reset! emoji-counts {})
    (emoji-handler "!!" nil {:content "<:unicorn:125> and <:haskell:456>"})
    (timbre/infof "Emoji : %s" @emoji-counts)
    (is (= (get @emoji-counts "<:unicorn:125>") 1))
    (is (= (get @emoji-counts "<:haskell:456>") 1))))
