(ns demo-blog.util
  (:require
    [cheshire.core :as json]))


(defn json-str
  "Convert given data (should be valid JSON data) to a JSON string."
  ^String
  [data]
  (json/generate-string data))


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
