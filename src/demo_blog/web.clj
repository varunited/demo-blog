(ns demo-blog.web
  (:require
    [demo-blog.border     :as    border]
    [demo-blog.service    :as    service]
    [demo-blog.util       :as    util]
    [demo-blog.validation :as    v]
    [clojure.spec.alpha   :as    s]
    [compojure.core       :refer [defroutes GET POST DELETE]]
    [compojure.route      :refer [not-found]]
    [promenade.core       :as    prom]
    [ringbelt.error       :as    error]
    [ringbelt.request     :as    req]
    [ringbelt.response    :as    res]))

(def lookup-camel->kebab {"heading" :heading
                          "content" :content
                          "emailId" :email-id})

(def camel->kebab (partial util/replace-keys lookup-camel->kebab))

(def lookup-kebab->camel {:story-id "storyId"})

(def kebab->camel (partial util/replace-keys lookup-kebab->camel))




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
      (border/err-handler input)
      (res/json-response {:status 200 :data (service/list-stories
                                              (util/clean-uuid owner-id))}))))


;; ---- Save story ----

(s/def ::heading  (s/and string?
                    #(not (empty? %))
                    #(<= (count %) 20)))
(s/def ::content  (s/and string? #(not (empty? %))))
(s/def ::email-id (s/and string? #(not (empty? %))))
(s/def ::owner-id (s/and string? #(not (empty? %))))

(def story-spec (s/keys :req [::heading ::content ::email-id]))

(defn validate-new-story-map [owner-id {:keys [heading content email-id] :as story-map}]
  (if (and
        (s/valid? ::owner-id owner-id)
        (s/valid? story-spec {::heading   heading
                              ::content   content
                              ::email-id  email-id}))
    story-map
    {:tag :bad-input :message "Bad input"}))

(defn content-type? [request]
  (get-in request [:headers "content-type"]))

(defn save-story [request owner-id]
  (if (= (content-type? request) "application/json")
    (let [payload (->> request
                    req/read-json-body
                    camel->kebab
                    (validate-new-story-map owner-id))]
      (if (contains? payload :tag)
        (border/err-handler payload)
        (let [service-response (service/save-story (util/clean-uuid owner-id) payload)]
          (if (contains? service-response :tag)
            (border/err-handler service-response)
            (res/json-response {:status 201 :data (kebab->camel service-response)})))))
    (border/err-handler {:tag :bad-input :message "Expected content-type: application/json"})))

;; -------------------------------------------------------------------------------------

(defn m-validate-new-story-input [owner-id {:keys [heading
                                                   content
                                                   email-id] :as story-map}]
  (if (and
        (s/valid? ::owner-id owner-id)
        (s/valid? story-spec {::heading   heading
                              ::content   content
                              ::email-id  email-id}))
    story-map
    (prom/fail {:error  "Bad input"
                :source :web
                :type   :bad-input})))

(defn m-save-story [request owner-id]
  (prom/either->> (v/m-validate-content-type request "application/json")
      v/m-read-json-body-as-map
      camel->kebab
      (m-validate-new-story-input owner-id)
      (service/m-save-story (util/clean-uuid owner-id))
      kebab->camel
      [border/failure->resp border/respond-201]))

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
      (border/err-handler input)
      (res/json-response {:status 200 :data (service/delete-story
                                              (util/clean-uuid owner-id) (util/clean-uuid story-id))}))))


;; ---- Routes ----


(defroutes app
  (GET "/owner/:owner-id"
    [owner-id :as request]          (list-stories request owner-id))

  (POST "/owner/:owner-id/new"
    [owner-id :as request]          (m-save-story request owner-id))

  (DELETE "/owner/:owner-id/delete/:story-id"
    [owner-id story-id :as request] (delete-story request owner-id story-id))

  (not-found "Sorry, page not found"))
