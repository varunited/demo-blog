(ns demo-blog.util
  (:require
    [cheshire.core :as json]))


(defn json-str
  "Convert given data (should be valid JSON data) to a JSON string."
  ^String
  [data]
  (json/generate-string data))


(defn parse-json-str
  "Convert a valid JSON string to a Clojure map"
  [data]
  (json/parse-string data))


(defn replace-keys
  [lookup x]
  (let [rk (partial replace-keys lookup)]
    (cond
      (map? x)    (reduce-kv (fn [m k v] (assoc m (if (contains? lookup k)
                                                    (get lookup k)
                                                    (rk k))
                                           (rk v)))
                    {} x)
      (vector? x) (mapv rk x)
      (set? x)    (set (map rk x))
      (list? x)   (doall (map rk x))
      (coll? x)   (doall (map rk x))
      :otherwise  x)))


(defn clean-uuid
  "Generate or convert UUID into a sanitized, lower-case form."
  (^String []
   (clean-uuid (.toString (java.util.UUID/randomUUID))))
  (^String [^String uuid]
   (if (nil? uuid)
     nil
     (let [n (.length uuid)
           ^StringBuilder b (StringBuilder. n)]
       (loop [i 0]
         (if (>= i n)
           (.toString b)
           (let [c (.charAt uuid i)]
             (when (Character/isLetterOrDigit c) ; ignore non-letter and non-numeric
               ;; make lower-case before adding
               (.append b (Character/toLowerCase c)))
             (recur (unchecked-inc i)))))))))
