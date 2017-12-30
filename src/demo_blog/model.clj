(ns demo-blog.model
  (:require
    [ringlet.util :as u]))

(defmacro defrecord+
  "clojure.core/defrecord on steroids. For a record name `Foo` add the following root level vars:
  Foo?     (fn [x]) -> boolean (return true when x is an instance of `Foo`)
  map+>Foo (fn [x]) -> new instance of Foo that requires all `Foo` attributes"
  [record-name [& attrs]]
  (u/expected symbol? "record name symbol" record-name)
  (u/expected (partial every? symbol?) "every attribute name to be a symbol" attrs)
  (let [dfactory (symbol (str "map->" record-name))
        sfactory (symbol (str "map+>" record-name))
        validate (symbol (str record-name "?"))
        at-keys (mapv keyword attrs)]
    `(do
       (defrecord ~record-name [~@attrs])
       (defn ~validate [x#] (instance? ~record-name x#))
       (defn ~sfactory [opts#]
         (u/expected map? "an attribute map" opts#)
         (when-not (every? (partial contains? opts#) ~at-keys)
           (u/expected ~(str "all attributes to construct " record-name ": " at-keys) (keys opts#)))
         (~dfactory opts#)))))


(defrecord+ NewArticle [heading
                        content
                        image-url])
