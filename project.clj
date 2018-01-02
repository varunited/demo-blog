(defproject     demo-blog "0.1.0-SNAPSHOT"
  :description  "A server-side webapp for IN-Clojure'18"
  :url          "https://github.com/varunited/demo-blog"
  :license      {:name "Eclipse Public License"
                 :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [ring/ring "1.6.3"]
                 [compojure "1.6.0"]
                 [clj-dbcp  "0.9.0"]
                 [asphalt   "0.6.2"]
                 [cheshire  "5.8.0"]
                 [mysql/mysql-connector-java "6.0.2"]
                 [ringlet "0.1.0-SNAPSHOT"]
                 [org.clojure/core.match "0.3.0-alpha5"]]
  :main         demo-blog.init
  :profiles     {:dev
                 {:main demo-blog.init/-dev-main}})
