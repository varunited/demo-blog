(ns demo-blog.init
  (:require
    [demo-blog.web          :as    web]
    [ring.adapter.jetty     :as    jetty]
    [ring.middleware.reload :refer [wrap-reload]]))


(defn -main []
  (jetty/run-jetty web/app
    {:port 9001}))


(defn -dev-main []
  (jetty/run-jetty (wrap-reload #'web/app)
    {:port 9001}))
