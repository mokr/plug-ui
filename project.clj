(defproject net.clojars.mokr/plug-ui "0.1.0-SNAPSHOT"
  :description "Reusable UI components for CLJS + Reagent + re-frame projects"
  :url "https://github.com/mokr/plug-ui"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[com.taoensso/timbre "5.2.1" :scope "provided"]
                 [org.clojure/clojure "1.11.1" :scope "provided"]
                 [org.clojure/clojurescript "1.11.51" :scope "provided"]
                 [net.clojars.mokr/plug-utils "0.1.0-SNAPSHOT" :scope "provided"]
                 [re-frame "1.2.0" :scope "provided"]
                 [reagent "1.1.1" :scope "provided"]]
  :repl-options {:init-ns plug-ui.core})
