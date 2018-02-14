(ns theproject.core
  (:gen-class)
  (:require [clojure.data.json :as json])
  (:require [cheshire.core :refer :all])
  (:require [datomic.api :as d]))

(def data_output {:job_assigned {:job_id "job id" :agent_id "agent id"}})

(defn read-json-data [archivepath] 
  (parse-string (slurp archivepath) true))

(def archivepath "resource/sample-input.json")

(def datareceived (read-json-data archivepath))

(defn get-agents [coll] (filter #(:new_agent %) coll))

(defn get-jobs [coll] (filter #(:new_job %) coll))

(defn get-jobs-requested [coll] (filter #(:job_request %) coll))

(defn get-urgent-jobs [coll]
  (filter #(= (:urgent (:new_job %)) true) coll))

(defn get-noturgent-jobs [coll] 
  (filter #(= (:urgent (:new_job %)) false) coll))

;;Don't use concat https://stuartsierra.com/2015/04/26/clojure-donts-concat
(defn prepare-coll [urgent_coll noturgent_coll] 
  (into urgent_coll noturgent_coll))

(defn select-available-agent [coll_jobs_requested coll_agents agents_list] 
  (cond 
    (or (empty? coll_jobs_requested) (empty? coll_agents)) agents_list
    (= (:agent_id (:job_request (first coll_jobs_requested)) (:id (:new_agent (first coll_agents))))) 
      (recur (rest coll_jobs_requested) (rest coll_agents) (conj agents_list (first coll_agents))) 
    :else (recur coll_jobs_requested (rest coll_agents) agents_list)))

(select-available-agent )




; (defn get-available-agents [coll_agents :job_request])
  
; (defn availa)

; (defn select-agent [coll_agent coll_job_request job] 
;   (loop []))


; (defn dequeue [coll]
;  (let  [output ()]
;   ( )))

;(println (get-urgent-jobs (get-jobs datareceived)))
;(println (get-noturgent-jobs (get-jobs datareceived)))


; (println (:primary_skillset (:new_agent (first (get-agents datareceived)))))
; (println (empty? (:secondary_skillset (:new_agent (first (get-agents datareceived))))))



; (println (prepare-coll (get-noturgent-jobs (get-jobs datareceived)) (get-urgent-jobs (get-jobs datareceived))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args] 

  ; (let [in (slurp *in*)]
  ;   (println (parse-string in)))

  ;; Reciving data, done!
  ;(def data-readed (parse-string (slurp *in*)))
)


