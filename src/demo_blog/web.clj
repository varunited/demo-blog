(ns demo-blog.web
  (:require
    [compojure.core  :refer [defroutes GET POST DELETE]]
    [compojure.route :refer [not-found]]
    [ring.adapter.jetty     :as    jetty]
    [ring.middleware.reload :refer [wrap-reload]]))


(defn home [request]
  {:status 200
   :body "Hello, World!"
   :headers {}})


(defn add-story [request]
  {:status 200
   :body "Hello, World!"
   :headers {}})


(defn delete-story [request]
  {:status 200
   :body "Hello, World 123!"
   :headers {}})


(defroutes app
  (GET "/" [] home)
  (POST "/user/:user-id/new" [] add-story)
  (DELETE "/user/:user-id/delete/:story-id" [] delete-story)
  (not-found "Sorry, page not found"))


(defn -main []
  (jetty/run-jetty app {:port 5000}))


(defn -dev-main []
  (jetty/run-jetty (wrap-reload #'app)
    {:port 5000}))
