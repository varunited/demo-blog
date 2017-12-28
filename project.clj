(defproject     demo-blog "0.1.0-SNAPSHOT"
  :description  "A server-side webapp for IN-Clojure'18"
  :url          "https://github.com/varunited/demo-blog"
  :license      {:name "Eclipse Public License"
                 :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-jetty-adapter "1.6.3"]]
  :main         demo-blog.core)
