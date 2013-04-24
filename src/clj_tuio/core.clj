(ns clj-tuio.core
  (:import (com.illposed.osc OSCPortIn OSCListener OSCMessage))
  (:use (clj-tuio util)))

(def tuio-2Dcur-address "/tuio/2Dcur")

(defn- set-command [args]
  "Handle an incoming fseq-command"
  (let [id (.longValue (aget args 1))
        x  (.floatValue (aget args 2))
        y  (.floatValue (aget args 3))]
    )

  )

(defn- alive-command [_]
  "Handle an incoming alive-command"
  nil)
(defn- fseq-command [_]
  "Handle an incoming fseq-command"
  nil)

(defn- listener [new-pointer remove-pointer move-pointer]
  (proxy [OSCListener] []
    (acceptMessage [time message]
      (let [arguments (.getArguments message)
            command (aget arguments 0)
            address (.getAddress message)]
        (case (address)
          tuio-2Dcur-address
          ( (case (command)
              "set" set-command
              "alive" alive-command
              "fseq" fseq-command) arguments)
          ;; TODO; log a warning; default case
          nil
          )))))

(defn- receiver [port]
  (OSCPortIn. port))

(defn- default-pointer-handler [_]
  "A default handler for pointer events"
  nil)

(defn start [port & {:as handlers}]
  (let [{:keys [new-pointer
                remove-pointer
                move-pointer]
         :or {new-pointer default-pointer-handler
              remove-pointer default-pointer-handler
              move-pointer default-pointer-handler}}
        handlers]
    (print move-pointer)
    (doto (receiver port)
      (.addListener ,,, tuio-2Dcur-address
                        (listener new-pointer remove-pointer move-pointer))
      (.startListening ,,,))))

(defn stop [receiver]
  (doto receiver
      (.stopListening)
      (.close))
  nil)
