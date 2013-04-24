(ns clj-tuio.util)

(def printer (agent nil))

(let [repl-out *out*]
  (defn repl-print [& args]
    (send printer (fn [_]
                    (binding [*out* repl-out]
                      (apply println args))))
    nil))

(defmacro dbg[x] `(let [x# ~x] (repl-print "dbg:" '~x "=" x#) x#))
