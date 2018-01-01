(ns demo-blog.db
  (:require
    [demo-blog.util :as util]
    [asphalt.core   :as a]
    [clj-dbcp.core  :as dbcp]))

(def db-spec
  {:datasource
   (dbcp/make-datasource
     {:classname "com.mysql.cj.jdbc.Driver"
      :jdbc-url  "jdbc:mysql://localhost:3306/demo_blog?useSSL=false"
      :user      "demo_blog"
      :password  "password"})})


(a/defsql sql-save-story
  "INSERT INTO stories
   (owner_id, story_id, story_json)
   VALUES
   (unhex($owner-id), unhex($story-id), $story-json)")


(defn save-story
  [owner-id story-id new-story]
  (let [story-map {:owner-id   owner-id
                   :story-id   story-id
                   :story-json (util/json-str new-story)}]
    (sql-save-story db-spec story-map)))


(a/defsql sql-delete-personal-data
  "UPDATE stories
      SET is_deleted = true, story_json = NULL
    WHERE owner_id = unhex($owner-id)
      AND story_id = unhex($story-id)")


(defn delete-story
  [owner-id story-id]
  (sql-delete-personal-data db-spec {:owner-id owner-id
                                     :story-id story-id}))
