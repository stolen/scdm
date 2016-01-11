; -*- mode: clojure -*-
(set-env!
  :source-paths   #{"src"}
  :resource-paths #{"html"}
  :dependencies '[
                  [org.clojure/clojure "1.7.0" :scope "provided"]
                  [org.clojure/clojurescript "1.7.228"]

                  [adzerk/boot-cljs "1.7.170-3" :scope "test"]
                  [adzerk/boot-reload    "0.4.2"      :scope "test"]
                  [pandeiro/boot-http    "0.7.0"      :scope "test"]

                  [adzerk/boot-cljs-repl "0.3.0"]
                  [com.cemerick/piggieback "0.2.1"  :scope "test"]
                  [weasel                  "0.7.0"  :scope "test"]
                  [org.clojure/tools.nrepl "0.2.12" :scope "test"]

                  [org.omcljs/om "0.9.0"]
                  [racehub/om-bootstrap "0.5.3"]
                  ])

(require '[pandeiro.boot-http :refer :all]
         '[adzerk.boot-reload    :refer [reload]]
         '[adzerk.boot-cljs      :refer [cljs]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]])

(deftask dev []
  (comp
    (serve :dir "target/")
    (watch)
    (reload)
    (cljs-repl)
    (cljs :source-map true :optimizations :none)
    (target :dir #{"target"})
))

(deftask rel []
  (comp
    (cljs :optimizations :advanced)
    (target :dir #{"rel_target"})
))
