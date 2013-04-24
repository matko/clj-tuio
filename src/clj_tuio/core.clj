(ns clj-tuio.core
  (:import (com.illposed.osc OSCPortIn OSCListener OSCMessage))
  (:use clj-tuio.util))

(def tuio-address "/tuio/2Dcur")

(defn- listener []
(proxy [OSCListener] []
  (acceptMessage [time message]
    (let [arguments (.getArguments message)]
      (repl-print time (aget arguments 0))))))

(defn- receiver [port]
  (OSCPortIn. port))

(defn start [port]
  (doto (receiver port)
       (.addListener ,,, tuio-address (listener))
       (.startListening ,,,)))

(defn stop [receiver]
  (doto receiver
      (.stopListening)
      (.close))
  nil)
