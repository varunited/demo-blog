(ns demo-blog.service
  (:require
    [demo-blog.db   :as db]
    [demo-blog.util :as util])
  (:import
    [java.sql SQLException]))


(defn list-stories
  [owner-id]
  (try
    (let [stories (db/list-stories-by-id owner-id)]
      {:stories stories})
    (catch SQLException e
      (throw (ex-info "Unable to find stories" {})))))


(defn save-story
  [owner-id {:keys [heading
                    content
                    image-url]
             :as new-story}]
  (try
    (let [story-id (util/clean-uuid)]
      (do (db/save-story owner-id story-id new-story)
          {:story-id story-id}))
    (catch SQLException e
      (throw (ex-info "Unable to save new story" {})))))


(defn delete-story
  [owner-id story-id]
  (try
    (do (db/delete-story owner-id story-id)
        {:deleted? true})
    (catch SQLException e
      (throw (ex-info "Unable to delete new-story" {})))))
