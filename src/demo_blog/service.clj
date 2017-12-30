(ns demo-blog.service
  (:require
    [demo-blog.db   :as db]
    [demo-blog.util :as util]))

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
    :otherwise         (let [story-id (util/clean-uuid)]
                         (if (= 1 (db/save-story owner-id story-id new-story))
                           {:story-id story-id}
                           {:tag :server-error :message "Unable to save new-story"}))))
