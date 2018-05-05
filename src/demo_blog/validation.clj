(ns demo-blog.validation
  (:require
    [promenade.core   :as prom]
    [stringer.core    :as stringer]
    [ringbelt.request :as req]))


(defn m-validate-content-type [request expected-content-type]
  (let [content-type (get-in request [:headers "content-type"])]
    (if (= expected-content-type content-type)
      request
      (prom/fail {:error  (stringer/strfmt "Expected header 'Content-type: %s', but found %s"
                            expected-content-type
                            (if (some? content-type)
                              (pr-str content-type)
                              "missing"))
                  :source :web
                  :type   :bad-input}))))

(defn m-read-json-body-as-map [request]
  (let [input-map (req/read-json-body request)]
    (if (map? input-map)
      input-map
      (prom/fail {:error  (stringer/strcat "Expected request body JSON to be a map, but found "
                            (pr-str input-map))
                  :source :web
                  :type   :bad-input}))))
