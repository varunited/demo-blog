(ns demo-blog.service
  (:require
    [demo-blog.db   :as db]
    [demo-blog.util :as util])
  (:import
    [java.sql SQLException]))


(defn delete-story
  [owner-id story-id]
  (cond
    (empty? owner-id) {:tag :bad-input :message "Invalid owner-id"}
    (empty? story-id) {:tag :bad-input :message "Invalid story-id"}
    :otherwise        (try
                        (do (db/delete-story owner-id story-id)
                            {:deleted? true})
                         (catch SQLException e
                           (throw (ex-info "Unable to delete new-story" {}))))))


(defn save-story
  [owner-id {:keys [heading
                    content
                    image-url]
             :as new-story}]
  (cond
    (empty? owner-id)  {:tag :bad-input :message "Invalid owner-id"}
    (empty? heading)   {:tag :bad-input :message "Invalid heading"}
    (empty? content)   {:tag :bad-input :message "Invalid content"}
    (empty? image-url) {:tag :bad-input :message "Invalid image-url"}
    :otherwise         (try
                         (let [story-id (util/clean-uuid)]
                           (do (db/save-story owner-id story-id new-story)
                               {:story-id story-id}))
                         (catch SQLException e
                           (throw (ex-info "Unable to save new story" {}))))))
