(ns sequiturish-demo.core
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [reagent.core :as r]
              [goog.string :refer [unescapeEntities]]
              [sequiturish.core :as sqtr]))
;; -------------------------
;; State

(defonce input-sequence (r/atom "abcdbcabcd"))

(def grammar (reaction (sqtr/sequiturish @input-sequence)))

(defn set-input-sequence! [s]
  (reset! input-sequence s))

;; -------------------------
;; Views

(defn sequence-input-form [val on-change]
  [:div.sequence-input-form
    [:label.sequence-input-label {:for "sequence-input"} "enter sequence:"]
    [:input.sequence-input {:id "sequence-input"
                            :value val
                            :on-change #(on-change (-> % .-target .-value))}]])

;; render grammar as graph
(declare render-symbols)

(defn render-terminal [s]
  [:span.symbol (str s)])

(defn render-rule [rule grammar]
  (let [rule-id (sqtr/rule-id rule)
        symbols (grammar rule-id)]
    [:span.rule
      [:div.rule__name rule-id]
      [:div.rule__symbols
        [:div.rule__symbols__bracket]
        (render-symbols symbols grammar)]]))

(defn render-symbols [symbols grammar]
  (map (fn [s]
         (if (sqtr/terminal? s)
           [render-terminal s]
           [render-rule s grammar]))
       symbols))

(defn grammar-graph [grammar]
  [:div.grammar-graph
    (render-symbols (sqtr/main-sequence grammar) grammar)])

;; render grammar as rule table
(defn rule-table [grammar]
  [:table.rule-table>tbody
    (map (fn [[rule-id symbols]]
           [:tr
             [:td
               (if (= rule-id sqtr/main-rule-id)
                 "S"
                 (str rule-id))]
             [:td
               (unescapeEntities "&rarr;")]
             [:td
               (map (fn [s]
                      (if (sqtr/terminal? s)
                        [:span.rule-table__symbol s]
                        [:span.rule-table__symbol.rule-table__symbol--rule (sqtr/rule-id s)]))
                    symbols)]])
         grammar)])

;; root component
(defn app []
  [:div
    [:h1.title
      [:a {:href "https://github.com/evgenykochetkov/sequiturish"} "sequiturish"]
      " demo: grammar visualizer"]
    [:p.description
      [:a {:href "http://www.sequitur.info/"} "Sequitur"]
      " is a method for inferring compositional hierarchies from sequences.
       It detects repetition and factors it out by forming rules in a grammar.
       The rules can be composed of non-terminals, giving rise to a hierarchy."]
    [sequence-input-form @input-sequence set-input-sequence!]
    [grammar-graph @grammar]
    [rule-table @grammar]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
