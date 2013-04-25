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
    (if (alive? id)
      (unless (= (retrieve id) detected-pointer)
              (move-pointer id detected-pointer))
      (new-pointer id detected-pointer))
    (add! id detected-pointer)))

(defn- alive-command
  "Handle an incoming alive-command"
  [arguments remove-pointer]
  (remove-some! (set arguments) remove-pointer))

(defn- fseq-command
  "Handle an incoming fseq-command"
  [arguments]
  nil)

(defn- source-command
  "Handle an incoming source-command"
  [arguments]
  nil)

(defn- listener [new-pointer remove-pointer move-pointer]
  (letfn [(set [a]
            (set-command a new-pointer move-pointer))
          (alive [a]
            (alive-command a remove-pointer))
          (fseq [a]
            (fseq-command a))
          (source [a]
            (source-command a))]
    (reify OSCListener
      (acceptMessage [this time message]
        (let [arguments (.getArguments message)
              command (aget arguments 0)
              address (.getAddress message)]
           (({:set set
                :alive alive
                :fseq fseq
                :source source} (keyword command)) arguments))))))

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
