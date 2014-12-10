(defproject ticker "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 ;;[clj-time "0.8.0"]
                 [korma "0.4.0"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [com.oracle/ojdbc14 "10.2.0.5.0"]
                 [com.taoensso/carmine "2.8.0"]
                 ;;[clojurewerkz/quartzite "1.3.0"]
                 ;;[jarohen/chime "0.1.6"]
                 ]
  ;;:main ^:skip-aot ticker.core
  :main ticker.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
