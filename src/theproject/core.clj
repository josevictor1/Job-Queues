(ns theproject.core
  (:gen-class)
  (:require [clojure.data.json :as json])
  (:require [cheshire.core :refer :all])
  (:require [datomic.api :as d]))

(def data_output {:job_assigned {:job_id "job id" :agent_id "agent id"}})

(defn read-json-data [archivepath] 
  (parse-string (slurp archivepath) true))

(def archivepath "resource/sample-input.json")

(def outputpath "resource/sample-output.json")

(def datareceived (read-json-data archivepath))

(def output (read-json-data outputpath))

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
    (or (empty? coll_jobs_requested) (empty? coll_agents)) 
      agents_list
    (= (:agent_id (:job_request (first coll_jobs_requested)) (:id (:new_agent (first coll_agents))))) 
      (recur (rest coll_jobs_requested) (rest coll_agents) (conj agents_list (first coll_agents))) 
    :else 
      (recur coll_jobs_requested (rest coll_agents) agents_list)))
;Test: OK
;(println (select-available-agent (get-jobs-requested datareceived) (get-agents datareceived) '()))

;; posso comparar diretamente da lista de trabalhos assigned

(defn assign-job [job agent]
  (conj '() {:job_assigned {:job_id (:id (:new_job job)) :agent_id (:id (:new_agent agent))}}))
;Test: OK
;(println (assign-job (first (get-jobs datareceived)) (first (get-agents datareceived))))
 
(defn primary_skillset [job available_agent]
  (some? (some #{(:type (:new_job job))} (:primary_skillset (:new_agent agent)))))
;Test: OK
;(println (primary_skillset (first (get-jobs output)) (select-available-agent (get-jobs-requested datareceived) (get-agents datareceived) '() )))

(defn secondary_skillset [job available_agent]
  (some? (some #{(:type (:new_job job))} (:secondary_skillset (:new_agent available_agent)))))
;Test: OK 
;(println (secondary_skillset (first (get-jobs datareceived)) (select-available-agent (get-jobs-requested datareceived) (get-agents datareceived) '() )))


; (println (first (get-jobs datareceived)))
; (println (select-available-agent (get-jobs-requested datareceived) (get-agents datareceived) '() ))
; (println (:type (:new_job (first (get-jobs datareceived)))))

; (println (select-available-agent (get-jobs-requested datareceived) (get-agents datareceived) '() ))

; (println (= (some #{(:type (:new_job (first (get-jobs datareceived))))} (select-available-agent (get-jobs-requested datareceived) (get-agents datareceived) '() ))))
; (println (= (some #{(:type (:new_job (first (get-jobs datareceived))))} [])))
; (println (= (:type (:new_job (first (get-jobs datareceived)))) (first [])))

(defn get-id-job [coll_jobs_assigned] (:job_id (:job_assigned (first coll_jobs_assigned))))
;Test: OK
;(println (get-id-job output))

(defn get-id-agent [coll_jobs_assigned] (:agent_id (:job_assigned (first coll_jobs_assigned))))
;Test: OK
;(println (get-id-agent output))

(defn job-agent-assigned? [coll_jobs_assigned val f]
  (cond 
    (= val (f coll_jobs_assigned))
      true
    (empty? coll_jobs_assigned)
      false
    :else
      (recur (rest coll_jobs_assigned) val f)))
;Test:OK
; (println (job-agent-assigned? output "8ab86c18-3fae-4804-bfd9-c3d6e8f66260" get-id-agent))
; (println (job-agent-assigned? output "f26e890b-df8e-422e-a39c-7762aa0bac36" get-id-job))

(defn assinged? [coll_jobs_assigned job agent] 
  (and (job-agent-assigned? coll_jobs_assigned job get-id-job)(job-agent-assigned? coll_jobs_assigned agent get-id-agent)))
;Test:
;(println (assinged? output "690de6bc-163c-4345-bf6f-25dd0c58e864" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"))
;(println (assinged? output "f26e890b-df8e-422e-a39c-7762aa0bac36" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"))

; (defn job-assigned? [coll_jobs_assigned job]
;   (cond 
;     (= (:id (:new_job  job)) (:job_id (:job_assigned (first coll_jobs_assigned))))
;       true
;     (empty? coll_jobs_assigned)
;       false
;     :else
;       (recur (rest coll_jobs_assigned) job)))

; (defn agent-assigned? [coll_jobs_assigned agent] 
;   (cond 
;     (= (:id (:new_agent agent)) (:agent_id (:job_assigned (first coll_jobs_assigned))))
;       true
;     (empty? coll_jobs_assigned)
;       false
;     :else
;       (recur (rest coll_jobs_assigned) agent)))



; (defn select-skill [coll_jobs coll_agents_available coll_jobs_assigned f] 
;   (cond 
;     (or (empty? coll-jobs) (empty? coll_agents_available))
;       coll_jobs_assigned
;     (and (not (assinged? coll_jobs_assigned (first coll_jobs) (first coll_agents_available))) (f (first coll_jobs) (first coll_agents_available)))
;       (recur (rest coll_jobs) (rest coll_agents_available) (conj coll_jobs_assigned (assign-job (first coll_jobs) (first coll_agents_available))) f)
;     (and (job-assigned? coll_jobs_assigned (first coll_jobs)) (not (agent-assigned? coll_agents_avaible)))
;       (recur (rest coll_jobs) coll_agents_available coll_jobs_assigned f)
;     (and (not (job-assigned? coll_jobs_assigned (first coll_jobs))) (agent-assigned? coll_agents_avaible))
;       (recur (rest coll_jobs) coll_agents_available coll_jobs_assigned f)))

(defn select-skill [coll_jobs coll_agents_available coll_jobs_assigned f] 
  (cond 
    (or (empty? coll_jobs) (empty? coll_agents_available))
      coll_jobs_assigned
    (and (not (assinged? coll_jobs_assigned (first coll_jobs) (first coll_agents_available))) (f (first coll_jobs) (first coll_agents_available)))
      (recur (rest coll_jobs) (rest coll_agents_available) (conj coll_jobs_assigned (assign-job (first coll_jobs) (first coll_agents_available))) f)
    (and (job-agent-assigned? coll_jobs_assigned (:id (:new_job (first coll_jobs))) get-id-job) (not (job-agent-assigned? coll_jobs_assigned (:id (:new_agent (first coll_agents_available))) get-id-agent)))
      (recur (rest coll_jobs) coll_agents_available coll_jobs_assigned f)
    (and (not (job-agent-assigned? coll_jobs_assigned (:id (:new_job (first coll_jobs))) get-id-job)) (job-agent-assigned? coll_jobs_assigned (:id (:new_agent (first coll_agents_available))) get-id-agent))
      (recur (rest coll_jobs) coll_agents_available coll_jobs_assigned f)))


      (select-skill (get-jobs datareceived) (select-available-agent (get-jobs-requested datareceived) (get-agents datareceived) teste1) teste primary_skillset)
(def teste '())
(def teste1 '())
(println (select-skill (get-jobs datareceived) (select-available-agent (get-jobs-requested datareceived) (get-agents datareceived) teste1) teste primary_skillset))      


; (println (check-skill ["asdf" "jklç" "qwer" "poiu"] "jklç"))
 
; (defn get-available-agents [coll_agents :job_request])
  
; (defn availa)

; (defn select-agent [coll_agent coll_job_request job] 
;   (loop []))


; (defn dequeue [coll]
;  (let  [output ()]
;   ( )))

;(println (get-urgent-jobs (get-jobs datareceived)))
;(println (get-noturgent-jobs (get-jobs datareceived)))


(defn -main
  "I don't do a whole lot ... yet."
  [& args] 

  ; (let [in (slurp *in*)]
  ;   (println (parse-string in)))

  ;; Reciving data, done!
  ;(def data-readed (parse-string (slurp *in*)))
)


