(ns ticker.core
  ;;(:import (java.time LocalDateTime))
  (:import (java.util TimerTask Timer))
  (:use [korma.db])
  (:use [korma.core])
  (:require [taoensso.carmine :as car :refer (wcar)])
  ;;(:require [clojurewerkz.quartzite.scheduler :as qs])
  ;;(:require [clojurewerkz.quartzite.triggers :as qt])
  (:gen-class))

(declare ora
         t-tick-values
         t-tick-nodes
         redis-connection)

;;(defn now [] (LocalDateTime/now))

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

(defentity t-tick-values
  (table :T_1319_SKILL_NUMBER)
  (pk :F_OPER_TIME)
  (entity-fields :F_OPER_TIME
                 :F_SKILL_NUMBER)
  ;;(modifier "SYSDATE")
  )

(defentity t-tick-nodes
  (table :T_1319_SKILLINFO)
  (entity-fields :F_START_NUM
                 :F_LEFT_NUM))

(defn select-t-tick-values
  []
  (select t-tick-values
          (fields [:F_OPER_TIME :F_SKILL_NUMBER])
          (where (= :F_OPER_TIME
                    (sqlfn T_NOW)))))

(defn select-t-tick-nodes
  [c]
  (let [s (select t-tick-nodes
                  (where (> :F_LEFT_NUM 0))
                  (order :F_START_NUM :ASC))]
    (when-not (nil? s)
      (let [n (long (:F_START_NUM (first s)))]
        (if (<= c n)
          c
          n)))))

(defn renew-redis-tick
  "!todo: 
  1). compare with previous value
  if v <= previous then do nothing;
  2). if redis wrong then renew sms notification."
  [v]
  (redis* (car/set "skill_num_1319" v)))

(defn check-t-tick
  []
  (let [s (select-t-tick-values)]
    (when-not (empty? s)
      (let [v (long (:F_SKILL_NUMBER (first s)))
            n (select-t-tick-nodes v)]
        ;(println v)
        (println n)
        (renew-redis-tick n)
        ))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

