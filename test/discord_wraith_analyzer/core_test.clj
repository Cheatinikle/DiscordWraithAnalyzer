(ns discord-wraith-analyzer.core-test
  (:require [clojure.test :refer :all]
            [discord-wraith-analyzer.core :refer :all]
            [taoensso.timbre :as timbre]))

(deftest emoji-test
  (testing "Can it detect emojis?"
    (reset! emoji-counts {})
    (emoji-handler "!!" nil {:content ":unicorn: and :haskell:"})
    (is (= (get ":unicorn:" @emoji-counts) 1))
    (is (= (get ":haskell:" @emoji-counts) 1))))

