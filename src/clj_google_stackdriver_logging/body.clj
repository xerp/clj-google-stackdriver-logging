(ns clj-google-stackdriver-logging.body)

(defn request-body
  [resource log-entry]
  (let [log-entry (if (vector? log-entry) log-entry [log-entry])]
    {:resource resource
     :entries  log-entry}))
