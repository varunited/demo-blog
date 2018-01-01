(ns demo-blog.web
  (:require
    [demo-blog.model   :as    model]
    [demo-blog.service :as    service]
    [demo-blog.util    :as    util]
    [compojure.core    :refer [defroutes GET POST DELETE]]
    [compojure.route   :refer [not-found]]
    [ringlet.error     :as    error]
    [ringlet.request   :as    req]
    [ringlet.response  :as    res]))


(def err-handler (-> (fn [_] (res/text-500 "Server error"))
                   (error/tag-lookup-middleware error/default-tag-lookup)))


(defn list-stories
  [request]
  (let [owner-id (-> request
                   (get-in [:params :owner-id])
                   util/clean-uuid)]
    (res/json-response {:status 201 :data (service/list-stories owner-id)})))


(defn save-story
  [request]
  (let [{:strs [heading
                content
                image-url]} (req/read-json-body request)
        owner-id (-> request
                   (get-in [:params :owner-id])
                   util/clean-uuid)
        new-story (model/map+>NewArticle {:heading   heading
                                          :content   content
                                          :image-url image-url})]
    (res/json-response {:status 201 :data (service/save-story owner-id new-story)})))


(defn delete-story
  [request]
  (let [owner-id (-> request
                   (get-in [:params :owner-id])
                   util/clean-uuid)
        story-id (-> request
                   (get-in [:params :story-id])
                   util/clean-uuid)]
    (res/json-response {:status 200 :data (service/delete-story owner-id story-id)})))


(defroutes app
  (GET "/owner/:owner-id" [] list-stories)
  (POST "/owner/:owner-id/new" [] save-story)
  (DELETE "/owner/:owner-id/delete/:story-id" [] delete-story)
  (not-found "Sorry, page not found"))
