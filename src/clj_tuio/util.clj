(ns clj-tuio.util)

(def printer (agent nil))

(let [repl-out *out*
      p printer]
  (defn repl-print [& args]
    (send p (fn [_]
                    (binding [*out* repl-out]
                      (apply println args))))
    nil))

(defmacro dbg[x] `(let [x# ~x] (repl-print "dbg:" '~x "=" x#) x#))

(defmacro local-context []
  (let [symbols (keys &env)]
    (zipmap (map (fn [sym] `(quote ~sym)) symbols) symbols)))

(defn contextual-eval [context expr]
  (eval
   `(let [~@(mapcat (fn [[k v]] [k `'~v]) context)]
      ~expr)))

(defn readr [prompt exit-code]
  (let [input (clojure.main/repl-read prompt exit-code)]
    (if (= input ::t1)
      exit-code
      input)))

(defmacro break []
  `(clojure.main/repl
    :prompt #(print "debug=> ")
    :read readr
    :eval (partial contextual-eval (local-context))))
