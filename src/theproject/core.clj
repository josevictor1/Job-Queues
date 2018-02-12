(ns theproject.core
  (:gen-class)
  (:require [clojure.data.json :as json])
  (:require [cheshire.core :refer :all])
  (:require [datomic.api :as d])
)

;;hard way
;;  (use 'clojure.java.io)
;;  (with-open [wrtr (writer "/tmp/test.json")]
;;     (.write wrtr (json/write-str {:key1 "val1" :key2 "val2"})))
;;lein dep && lein compile

;;> (spit "/tmp/test.json" (json/write-str {:key1 "val1" :key2 "val2"}))

;;(defn read-json [path] (json/read-str (read-archive path)) :value-fn keyword)
;;(map #(cheshire/parse-string % true)
;;(line-seq (clojure.java.io/reader "data/json_data")))

(def archivepath "resource/teste.json")

(defn read-archive [path] (slurp path))

(defn read-json-data [archivepath] (parse-string (read-archive archivepath) true)) 

;;(map #(parse-string % true)
;;     (line-seq (clojure.java.io/reader "/Users/josevictorpereiracosta/Documents/secret/jobqueue/resource/jason_data.json")))

;;(defn teste [archivepath] (parse-string (slurp archivepath) true))

(def datareceived (read-json-data archivepath))


(defn get-agents [coll] (filter (fn [x] (:new_agent x)) coll))


(println "Jobs queue")
(defn get-jobs [coll] (filter (fn [x] (:new_job x)) coll))

(defn get-jobs-request [coll] (filter (fn [x] (:job_request x)) coll))

;;(println (get-agents datareceived))

;;(println (sort-by :urgent (get-jobs datareceived)))

;(println (map (= :urgent true) (get-jobs datareceived)))
;;(dequeue [coll-agent coll-job])

;;(println (get-jobs-request datarecived))

(defn is-urgent? [coll] 
  (if (= ((coll :new_job) :urgent) true) coll))

(defn get-urgent-jobs [coll] 
  (filter (fn [x] (not= nil x)) (map is-urgent? coll)))


  

(print (get-urgent-jobs (get-jobs datareceived)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
   
 ;; (print (apply conj (line-seq (java.io.BufferedReader. *in*))))
 
  )

