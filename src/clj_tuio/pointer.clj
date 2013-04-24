(ns clj-tuio.pointer)

(defrecord Pointer
    [x y])

(defonce pointers (atom {}))

(defn pointer [x y]
  (Pointer. x y))

(defn alive? [id]
  (contains? @pointers id))

(defn add! [id pointer]
  (swap! pointers #(assoc % id pointer)))

(defn- point-difference [m coll]
  (clojure.set/difference
   (set (keys m)) (set coll)))

(defn remove-selected! [alive-keys f]
  (swap! pointers
         #(let [current-pointers %
               difference (point-difference current-pointers alive-keys)]
           (map (partial apply f)
                (filter (comp difference key)
                        current-pointers))
           (apply dissoc current-pointers difference))))

(defn retrieve [id]
  (get @pointers id))
