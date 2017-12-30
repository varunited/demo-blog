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


(defn delete-story [request]
    {:status 200
     :body "Hello, World 123!"
     :headers {}})


(defroutes app
  (GET "/" [] home)
  (POST "/user/:owner-id/new" [] save-story)
  (DELETE "/user/:user-id/delete/:story-id" [] delete-story)
  (not-found "Sorry, page not found"))
