(ns ticker.core
  ;;(:import (java.time LocalDateTime))
  (:use [korma.db])
  (:use [korma.core])
  (:require [taoensso.carmine :as car :refer (wcar)])
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
  []
  (select t-tick-nodes
          (where (>= :F_LEFT_NUM 0))
          (order :F_START_NUM :ASC)))

(defn check-t-tick
  []
  (let [v (select-t-tick-values)
        n (:F_SKILL_NUMBER v)]
    (when-not (nil? v)
      v)))

(defn renew-redis-tick
  [v]
  (redis* (car/set "skill_num_1319" v)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
