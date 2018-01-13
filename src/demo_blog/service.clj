(ns demo-blog.service
  (:require
    [demo-blog.db   :as db]
    [demo-blog.util :as util]
    [promenade.core :as prom])
  (:import
    [java.sql SQLException]))


;; --- List stories ----

(defn list-stories
  [owner-id]
  (try
    (let [stories (db/list-stories-by-id owner-id)]
      {:stories stories})
    (catch SQLException e
      (throw (ex-info "Unable to find stories" {})))))


;; --- Save story ----

(defn valid-email-id? [email-id]
  (re-matches #".+\@.+\..+" email-id))


(defn save-story [owner-id {:keys [email-id] :as new-story}]
  (if-not (valid-email-id? email-id)
    {:tag :bad-input :message "Invalid email-id"}
    (try
      (let [story-id (util/clean-uuid)]
        (do (db/save-story owner-id story-id new-story)
            {:story-id story-id}))
      (catch SQLException e
        (throw (ex-info "Unable to save new story"
                 {:owner-id owner-id}))))))

;; ---------------------------------------------------------------

(defn m-save-story [owner-id {:keys [email-id] :as new-story}]
  (if-not (valid-email-id? email-id)
    (prom/fail {:error  "Invalid email-id"
                :source :service
                :type   :bad-input})
    (try
      (let [story-id (util/clean-uuid)]
        (do (db/save-story owner-id story-id new-story)
            {:story-id story-id}))
      (catch SQLException e
        (prom/fail {:error  "Unable to save new story"
                    :source :execution
                    :type   :unavailable})))))

;; --- Delete story ----

(defn delete-story
  [owner-id story-id]
  (try
    (do (db/delete-story owner-id story-id)
        {:deleted? true})
    (catch SQLException e
      (throw (ex-info "Unable to delete new-story" {})))))
