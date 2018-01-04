(ns demo-blog.web
  (:require
    [demo-blog.service  :as    service]
    [demo-blog.util     :as    util]
    [compojure.core     :refer [defroutes GET POST DELETE]]
    [compojure.route    :refer [not-found]]
    [ringlet.error      :as    error]
    [ringlet.request    :as    req]
    [ringlet.response   :as    res]))


;; ---- Validations ----

(defn content-type?
  [request]
  (get-in request [:headers "content-type"]))


(def err-handler (-> (fn [_] (res/text-500 "Server error"))
                   (error/tag-lookup-middleware error/default-tag-lookup)))


;; ---- List stories ----


(defn valid-list-stories-input?
  [owner-id]
  (if (empty? owner-id)
    {:tag :bad-input :message "Invalid owner-id"}
    "valid-input"))


(defn list-stories
  [request owner-id]
  (let [input (valid-list-stories-input? owner-id)]
    (if (contains? input :tag)
      (err-handler input)
      (res/json-response {:status 200 :data (service/list-stories
                                              (util/clean-uuid owner-id))}))))


;; ---- Save story ----


(defn validate-new-story-map
  [owner-id {:strs [heading
                    content
                    image-url]}]
  (cond
    (empty? owner-id)  {:tag :bad-input :message "Invalid owner-id asd"}
    (empty? heading)   {:tag :bad-input :message "Invalid heading asd"}
    (empty? content)   {:tag :bad-input :message "Invalid content asd"}
    (empty? image-url) {:tag :bad-input :message "Invalid image-url asd"}
    :otherwise         {:heading   heading
                        :content   content
                        :image-url image-url}))


(defn save-story
  [request owner-id]
  (if (= (content-type? request) "application/json")
    (let [payload (->> request
                    req/read-json-body
                    (validate-new-story-map owner-id))]
      (if (contains? payload :tag)
        (err-handler payload)
        (res/json-response {:status 201 :data (service/save-story
                                                (util/clean-uuid owner-id) payload)})))
    (err-handler {:tag :bad-input :message "Expected content-type: application/json"})))


;; ---- Delete story ----


(defn valid-delete-story-input?
  [owner-id story-id]
  (cond
    (empty? owner-id) {:tag :bad-input :message "Invalid owner-id"}
    (empty? story-id) {:tag :bad-input :message "Invalid story-id"}
    :otherwise        "valid-input"))


(defn delete-story
  [request owner-id story-id]
  (let [input (valid-delete-story-input? owner-id story-id)]
    (if (contains? input :tag)
      (err-handler input)
      (res/json-response {:status 200 :data (service/delete-story
                                              (util/clean-uuid owner-id) (util/clean-uuid story-id))}))))


;; ---- Routes ----


(defroutes app
  (GET "/owner/:owner-id"
    [owner-id :as request]          (list-stories request owner-id))

  (POST "/owner/:owner-id/new"
    [owner-id :as request]          (save-story request owner-id))

  (DELETE "/owner/:owner-id/delete/:story-id"
    [owner-id story-id :as request] (delete-story request owner-id story-id))

  (not-found "Sorry, page not found"))
