(ns demo-blog.border
  (:require
    [ringbelt.error    :as error]
    [ringbelt.response :as res]))

(def err-handler (-> (fn [_] (res/text-500 "Server error 123"))
                   (error/tag-lookup-middleware error/default-tag-lookup)))

(defn failure->resp [{:keys [error
                             source
                             type] :as failure}]
  (err-handler {:tag type :message error}))

(defn respond-201 [data]
  (res/json-response {:status 201 :data data}))
