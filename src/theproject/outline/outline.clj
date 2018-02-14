(ns theproject.core
    (:gen-class)
    (:require [clojure.data.json :as json])
    (:require [cheshire.core :refer :all])
    (:require [datomic.api :as d])
  )
  
  ;; to run:  cat resource/sample-input.json | lein trampoline run
  
  ;;hard way
  ;;  (use 'clojure.java.io)
  ;;  (with-open [wrtr (writer "/tmp/test.json")]
  ;;     (.write wrtr (json/write-str {:key1 "val1" :key2 "val2"})))
  ;;lein dep && lein compile
  
  ;;> (spit "/tmp/test.json" (json/write-str {:key1 "val1" :key2 "val2"}))
  
  ;;(defn read-json [path] (json/read-str (read-archive path)) :value-fn keyword)
  ;;(map #(cheshire/parse-string % true)
  ;;(line-seq (clojure.java.io/reader "data/json_data")))
  
  
  ;; Data tratament
  
  ;(defn read-archive [path] (slurp path))
  
  
  (def data_output {:job_assigned {:job_id "job id" :agent_id "agent id"}})
  
  
  (defn read-json-data [archivepath] 
    (parse-string (slurp archivepath) true))
  
  (def archivepath "resource/sample-input.json")
  
  (def datareceived (read-json-data archivepath))
  
  ;;(map #(parse-string % true)
  ;;     (line-seq (clojure.java.io/reader "/Users/josevictorpereiracosta/Documents/secret/jobqueue/resource/jason_data.json")))
  
  ;;(defn teste [archivepath] (parse-string (slurp archivepath) true))
  
  ; ;; With fn
  ; (defn get-agents [coll] (filter (fn [x] (:new_agent x)) coll))
  
  ; (defn get-jobs [coll] (filter (fn [x] (:new_job x)) coll))
  
  ; (defn get-jobs-request [coll] (filter (fn [x] (:job_request x)) coll))
  
  ;;With high order function
  
  
  
  
  ;;With if
  ; (defn get-urgent-jobs [coll] 
  ;   (filter (fn [x] (not= nil x)) (map is-urgent coll)))
  
  ;;Without if
  
  
  (defn get-agents [coll] (filter #(:new_agent %) coll))
  
  (defn get-jobs [coll] (filter #(:new_job %) coll))
  
  (defn get-jobs-request [coll] (filter #(:job_request %) coll))
  
  ; (defn is-urgent [coll] 
  ;   (= ((coll :new_job) :urgent) true))
  
  ; (defn is-not-urgent [coll] 
  ;   (= ((coll :new_job) :urgent) false))
  
  (defn get-urgent-jobs [coll]
    (filter #(= (:urgent (:new_job %)) true) coll))
  
  (defn get-noturgent-jobs [coll] 
    (filter #(= (:urgent (:new_job %)) false) coll))
  
  
  ;;Don't use concat https://stuartsierra.com/2015/04/26/clojure-donts-concat
  (defn prepare-coll [urgent_coll noturgent_coll] 
    (into urgent_coll noturgent_coll))
  
  
    
  (defn dequeue [coll]
   (let  [output ()]
    ( )))
  
  ;(println (get-urgent-jobs (get-jobs datareceived)))
  ;(println (get-noturgent-jobs (get-jobs datareceived)))
  (println (prepare-coll (get-noturgent-jobs (get-jobs datareceived)) (get-urgent-jobs (get-jobs datareceived))))
  
  ;  (defn dequeue [coll] 
  ;    (let [])
  
  (defn -main
    "I don't do a whole lot ... yet."
    [& args] 
  
    ; (let [in (slurp *in*)]
    ;   (println (parse-string in)))
  
    ;; Reciving data, done!
    ;(def data-readed (parse-string (slurp *in*)))
  )
  
  
  