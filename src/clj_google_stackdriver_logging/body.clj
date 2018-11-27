(ns clj-google-stackdriver-logging.body)

(defn request-body
  [resource log-entry]
  (let [assoc-log-entry (fn [log] (assoc log :resource resource
                                             :logName (str "projects/" (-> resource :labels :project_id) "/logs/" (log :log-name))
                                             (if (map? (log :data)) :jsonPayload :textPayload) (log :data)))
        log-entry (if (vector? log-entry) (map assoc-log-entry log-entry) [(assoc-log-entry log-entry)])]
    {:resource resource
     :entries  log-entry}))
