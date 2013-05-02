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

;; TESTAMENT TO STUPIDITY
;; (defn- point-difference [m coll]
;;   (clojure.set/difference
;;    (set (keys m)) (set coll)))

;; (defn remove-selected! [alive-keys f]
;;   (swap! pointers
;;          #(let [current-pointers %
;;                difference (point-difference current-pointers alive-keys)]
;;            (map (partial apply f)
;;                 (filter (comp difference key)
;;                         current-pointers))
;;            (apply dissoc current-pointers difference))))

(defn remove-some
  "Applies fn f to each entry in map m that has a key that isn't contained in set s.
Returns m without the entries whose keys are not present in s."
  ([m s]
      (remove-some m s (fn [_ _])))
  ([m s f]
      (apply dissoc m
             (for [[k v] m
                   :when (not (s k))]
               (do
             (f k v)
             k)))))

(defn remove-some!
  "Calls fn f with all [id , point] entries for which id is not present in set s"
  [s f]
  (swap! pointers #(remove-some % s f)))

(defn retrieve [id]
  (get @pointers id))
