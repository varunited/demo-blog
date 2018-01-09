(ns demo-blog.db
  (:require
    [demo-blog.util :as util]
    [asphalt.core   :as a]
    [asphalt.type   :as atype]
    [clj-dbcp.core  :as dbcp])
  (:import
    [com.fasterxml.jackson.core JsonParseException]))

(def db-spec
  {:datasource
   (dbcp/make-datasource
     {:classname "com.mysql.cj.jdbc.Driver"
      :jdbc-url  "jdbc:mysql://localhost:3306/demo_blog?useSSL=false"
      :user      "demo_blog"
      :password  "password"})})


;; --- List stories ---


(defn row->story [[story-id story-json]]
  (try
    {:story-id    (util/clean-uuid story-id)
     :story-json  (util/parse-json-str story-json)}
    (catch JsonParseException e
      (throw (ex-info "Error deserializing JSON data from database" {:story-id story-id})))))

(a/defsql sql-list-stories-by-id
  "SELECT hex(story_id), story_json
     FROM stories
    WHERE owner_id = unhex($owner-id)
      AND is_deleted = false"
  {:result-set-worker (partial a/fetch-rows {:row-maker (comp row->story atype/read-row)})})

(defn list-stories-by-id [owner-id]
  (sql-list-stories-by-id db-spec {:owner-id owner-id}))


;; ---- Save story ---


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


;; --- Delete story ---


(a/defsql sql-delete-personal-data
  "UPDATE stories
      SET is_deleted = true, story_json = NULL
    WHERE owner_id = unhex($owner-id)
      AND story_id = unhex($story-id)")


(defn delete-story
  [owner-id story-id]
  (sql-delete-personal-data db-spec {:owner-id owner-id
                                     :story-id story-id}))
