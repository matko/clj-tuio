(ns clj-tuio.core
  (:import (com.illposed.osc OSCPortIn OSCListener OSCMessage))
  (:use (clj-tuio util pointer))
  (:use [clojure.tools.logging :only (warn info debug)]))

(def tuio-2Dcur-address "/tuio/2Dcur")

(defmacro unless [condition & body]
  `(when (not ~condition)
     ~@body))

(defn- set-command [arguments new-pointer move-pointer]
  "Handle an incoming set-command"
  (let [id (.longValue (aget arguments 1))
        x  (.floatValue (aget arguments 2))
        y  (.floatValue (aget arguments 3))
        detected-pointer (pointer x y)]
    (add id detected-pointer)
    (if (alive? detected-pointer)
      (unless (= (retrieve id) detected-pointer)
              (move-pointer id detected-pointer))
      (new-pointer id detected-pointer))))

(defn- alive-command
  "Handle an incoming alive-command"
  [arguments remove-pointer]
  (remove-selected! (vec arguments) remove-pointer))

(defn- fseq-command
  "Handle an incoming fseq-command"
  [arguments]
  nil)

(defn- listener [new-pointer remove-pointer move-pointer]
  (proxy [OSCListener] []
    (acceptMessage [time message]
      (let [arguments (.getArguments message)
            command (aget arguments 0)
            address (.getAddress message)]
        (case (address)
          tuio-2Dcur-address
          ((case (command)
              "set"  #(set-command % new-pointer move-pointer)
              "alive" #(alive-command % remove-pointer)
              "fseq" fseq-command
              (fn [_] (warn "Fallthrough OSC command"))) arguments)
          (warn "Fallthrough TUIO address")
          )))))

(defn- receiver [port]
  (OSCPortIn. port))

(defn- default-pointer-handler [_ _]
  "A default handler for pointer events"
  (warn "Unsupplied pointer handler")
  nil)

(defn start
  "Starts listening for TUIO events on port, with optional named pointer-handler fn's :new-pointer, :remove-pointer and :move-pointer. Returns a handle to the event receiver."
  [port & {:as handlers}]
  (let [{:keys [new-pointer
                remove-pointer
                move-pointer]
         :or {new-pointer default-pointer-handler
              remove-pointer default-pointer-handler
              move-pointer default-pointer-handler}}
        handlers]
    (doto (receiver port)
      (.addListener ,,, tuio-2Dcur-address
                        (listener new-pointer remove-pointer move-pointer))
      (.startListening ,,,))))

(defn stop
  "Stops listening for TUIO events, and releases the port bound by receiver."
  [receiver]
  (doto receiver
      (.stopListening)
      (.close))
  nil)
