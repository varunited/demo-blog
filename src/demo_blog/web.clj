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


(defn home [request]
  (res/status {:body "Hello, World foo-bar!"}))


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
    (let [service-resp (service/save-story owner-id new-story)]
      (if (contains? service-resp :story-id)
        (res/json-response {:status 201 :data service-resp})
        (err-handler service-resp)))))


(defn delete-story
  [request]
  (let [owner-id (-> request
                   (get-in [:params :owner-id])
                   util/clean-uuid)
        story-id (-> request
                   (get-in [:params :story-id])
                   util/clean-uuid)]
    (let [service-resp (service/delete-story owner-id story-id)]
      (if (true? (:deleted? service-resp))
        (res/json-response {:status 201 :data service-resp})
        (err-handler service-resp)))))


(defroutes app
  (GET "/" [] home)
  (POST "/user/:owner-id/new" [] save-story)
  (DELETE "/user/:owner-id/delete/:story-id" [] delete-story)
  (not-found "Sorry, page not found"))
