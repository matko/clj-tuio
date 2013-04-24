(ns clj-tuio.pointer
  (:import (com.illposed.osc OSCPortIn OSCListener OSCMessage))
  (:use clojure.tools.logging))

(defrecord Pointer
    [x y])

(defonce pointers (atom {}))

(defn alive? [id]
  (contains? @pointers id))

(defn add [id pointer]
  (swap! pointers #(assoc % id pointer)))

(defn delete [id]
  (swap! pointers #(dissoc % id)))

(defn pointer [id]
  (get @pointers id))
