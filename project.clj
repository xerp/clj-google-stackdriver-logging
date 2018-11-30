(defproject clj-google-stackdriver-logging "1.0"
  :description "Google Stackdriver Logging api"
  :url "https://github.com/xerp/clj-google-stackdriver-logging"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.cemerick/url "0.1.1"]
                 [clj-http "3.9.1"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-google "0.2.7"]]
  :repositories [["releases" {:url           "https://clojars.org/repo"
                              :username      :env/CLOJAR_USERNAME
                              :password      :env/CLOJAR_PASSWORD
                              :sign-releases false}]])
