(ns clj-tuio.core
  (:import (com.illposed.osc OSCPortIn OSCListener OSCMessage))
  (:use clojure.tools.logging))
(def tuio-address "/tuio/2Dcur")

(defrecord Pointer
    [x y alive])

(def printer (agent nil))

(let [repl-out *out*]
  (defn repl-print [& args]
    (send printer (fn [_]
                    (binding [*out* repl-out]
                      (apply println args))))
    nil))

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
