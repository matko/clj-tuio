(ns clj-tuio.pointer-test
  (:use midje.sweet)
  (:use clojure.test
        clj-tuio.pointer))

(comment (fact "`split` splits strings on regular expressions and returns a vector"
               (str/split "a/b/c" #"/") => ["a" "b" "c"]
               (str/split "" #"irrelevant") => [""]
               (str/split "no regexp matches" #"a+\s+[ab]") => ["no regexp matches"]))

(let [empty-map {}
      empty-set #{}
      nil-fn (fn [_ _])
      example-map {:a 1 :b 2 :c 3}
      whole-set #{:a :b :c}
      sub-set #{:a :c}
      disjoint-set #{:d :e}
      overlap-set #{:b :c :e :f}
      rs-shortcut #(remove-some example-map % nil-fn)]
  (fact "`remove-some` leaves entries whose key are not present in the supplied set"
        (remove-some empty-map empty-set nil-fn) => empty-map
        (rs-shortcut empty-set) => empty-map
        (rs-shortcut whole-set) => example-map
        (rs-shortcut sub-set) => {:a 1 :c 3}
        (rs-shortcut disjoint-set) => empty-map
        (rs-shortcut overlap-set) => {:b 2 :c 3}))
