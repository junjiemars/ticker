(ns ticker.core
;;(:import (java.time LocalDateTime))
;;(:import (java.util TimerTask Timer))
(:import (java.util.concurrent Executors))
(:import (java.util.concurrent TimeUnit))
(:import (java.util.concurrent ScheduledExecutorService))
(:import (java.util.concurrent ScheduledFuture))
(:import (java.sql SQLException))
(:use [korma.db])
(:use [korma.core])
(:require [taoensso.carmine :as car :refer (wcar)])
;;(:require [clojurewerkz.quartzite.scheduler :as qs])
;;(:require [clojurewerkz.quartzite.triggers :as qt])
(:gen-class))

(declare ora
       tick-values
       tick-nodes
       redis-connection
       running)

;;(defn now [] (LocalDateTime/now))
(def running (ref true))

(def oracle-db {:classname "oracle.jdbc.driver.OracleDriver"
       :subprotocol "oracle"
       :subname "thin:@//localhost:1521/XE"
       :user "xws"
       :password "xws"})

(def redis-connect {:pool
                  {:host "localhost"
                   :port 6379
                   :db 1}})
(defmacro redis*
[& body]
`(car/wcar redis-connection  ~@body))

(defdb ora oracle-db)

(defentity tick-values
(table :T_1319_SKILL_NUMBER)
(pk :F_OPER_TIME)
(entity-fields :F_OPER_TIME
               :F_SKILL_NUMBER)
;;(modifier "SYSDATE")
)

(defentity tick-nodes
(table :T_1319_SKILLINFO)
(entity-fields :F_START_NUM
               :F_LEFT_NUM))

(defn select-tick-values
[]
(try (select tick-values
              (fields [:F_OPER_TIME :F_SKILL_NUMBER])
              (where (= :F_OPER_TIME
                        (sqlfn T_NOW))))
     (catch SQLException e
       (println e))))

(defn select-tick-nodes
[c]
(try (let [s (select tick-nodes
                     (where (> :F_LEFT_NUM 0))
                     (order :F_START_NUM :ASC))]
       (when-not (empty? s)
         (let [n (long (:F_START_NUM (first s)))]
           (if (<= c n)
             c
               n))))
       (catch SQLException e
         (println e))))

(defn renew-redis-tick
  "!todo: 
  1). compare with previous value
  if v <= previous then do nothing;
  2). if redis wrong then renew sms notification."
  [v]
  (try (redis* (car/set "skill_num_1319" v))
       (catch Exception e
         (println e))))

(defn check-tick
  []
  (let [s (select-tick-values)]
    (when-not (empty? s)
      (let [v (long (:F_SKILL_NUMBER (first s)))
            n (select-tick-nodes v)]
        ;(println v)
        (println n)
        (renew-redis-tick n)
        ))))

(defn ticker
  []
  (.scheduleWithFixedDelay
   (Executors/newScheduledThreadPool 1)
   check-tick
   0 5 TimeUnit/SECONDS))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (ticker))

