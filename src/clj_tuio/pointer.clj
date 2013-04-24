(ns clj-tuio.pointer
  (:import (com.illposed.osc OSCPortIn OSCListener OSCMessage))
  (:use clojure.tools.logging))

(defrecord Pointer
    [x y])

(defonce pointers (atom {}))

(defn pointer [x y]
  (Pointer. x y))

(defn alive? [id]
  (contains? @pointers id))

(defn add [id pointer]
  (swap! pointers #(assoc % id pointer)))

(defn delete [id]
  (swap! pointers #(dissoc % id)))

(defn retrieve [id]
  (get @pointers id))
