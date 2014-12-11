(ns ticker.core
  (:import (java.util.concurrent Executors))
  (:import (java.util.concurrent TimeUnit))
  (:import (java.util.concurrent ScheduledExecutorService))
  (:import (java.util.concurrent ScheduledFuture))
  (:import (java.sql SQLException))
  (:use [korma.db])
  (:use [korma.core])
  (:require [taoensso.carmine :as car :refer (wcar)])
  (:gen-class))

(declare ora
       red*
       running)

(def running (ref true))

(def oracle-db*
  (oracle {:subname "@10.32.122.13:1521:ecnDB1"
           :user "ecos0"
           :password "ecos0123"
           :naming {:keys clojure.string/lower-case
                    :fields clojure.string/upper-case
                    }
           }))

(def red* {:pool {}
           :spec {:host "10.32.237.145"
                  :port 6379
                  :db 1}})

(defmacro redis*
  [& body]
  `(car/wcar red*  ~@body))

(defdb ora oracle-db*)

(defn select-tick-values*
  []
  (try (exec-raw ora
                 ["SELECT T_1319_TICK_CNT FROM DUAL"]
                 :results)
       (catch SQLException e
         (println e))))

(defn insert-tick-sms-alerts*
  [c]
  (try (exec-raw ora
                 ["begin T_1319_SMS_ALERTS(?);end;"
                  [c]])
       (catch SQLException e
         (println e))))

(defn renew-redis-tick*
  "!todo: 
  1). compare with previous value
  if v <= previous then do nothing;
  2). if redis wrong then renew sms notification."
  [n]
  (try (redis* (car/set "skill_num_1319" n))
       (println (str "R#" n))
       n
       (catch Exception e
         (insert-tick-sms-alerts* (.getMessage e))
         (println e))))

(defn check-tick
  []
  (let [s (select-tick-values*)]
    (println s)
    (when-not (empty? s)
      (let [n (Long/parseLong (:t_1319_tick_cnt (first s)))]
        (println (str "D#" n))
        (renew-redis-tick* n)
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
  (println "ticking...")
  (ticker))

