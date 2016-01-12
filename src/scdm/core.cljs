(ns scdm.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [om-bootstrap.grid :as g]
            [om-bootstrap.panel :as p]
            [om-bootstrap.input :as i]
            [cljs.core.async :refer [put! chan <!]]
            [clairvoyant.core :as trace :include-macros true]
            ))

(enable-console-print!)

(def app-state
  (atom
    {:events
     [{:style "Hustle", :substyle "Social", :type :disco,  :name "Teplica",  :timetable [[:tue "20:30" "23:30"] [:sat "20:30" "00:30"]]}
      {:style "Hustle", :substyle "Sport",  :type :disco,  :name "Mossovet", :timetable [[:fri "20:30" "23:30"] [:sun "20:00" "00:00"]]}
      {:style "WCS",                        :type :disco,  :name "Ivara",    :timetable [[:sat "20:30" "23:30"]]}
      {:style "WCS",                        :type :course, :name "Lisoborie",:timetable [[:mon "20:30" "22:30"] [:thu "20:30" "22:30"]]}
      {:style "Zouk",                       :type :course, :name "Brazuka",  :timetable [[:mon "21:00" "22:30"] [:thu "21:00" "22:30"]]}
      ]
     :user-styles
     [
      {:style "Hustle", :substyle "Social"}
      ;{:style "Hustle", :substyle "Sport"}
      {:style "WCS"}
      ]
     }
    )
  )

(defn clean-style [src]
  (select-keys src [:style :substyle]) )

(defn clean-styles [coll]
  (reduce #(conj %1 (clean-style %2)) #{} coll) )

;(trace/trace-forms {:tracer trace/default-tracer}
(defn all-selections [data]
  (let [
        possible (clean-styles (:events data))
        selected (clean-styles (:user-styles data))
        ]
    (mapv #(assoc %1 :selected (contains? selected %1)) possible)
    )
  )
;)

(defn styles [state]
  (vec (reduce #(conj %1 (:style %2)) #{} (:events state))) )

(defn substyles [state style]
  (vec (for [e (:events state),
             :when (= (:style e) style),
             :when (:substyle e)]
         (:substyle e) )))

(defn style-selection [name selectors]
  (vec (filter #(if (and (= (:style %1) name) (:selected %1)) %1) selectors)) )

(defn style-matches [pattern obj]
  (= (select-keys obj (keys pattern)) pattern) )


(defn select-style [owner cleanstyle selected]
  (om/update-state! owner :styles (fn [oldstyles] (mapv #(if (style-matches cleanstyle %1) (assoc %1 :selected selected) %1) oldstyles)))
  )


(defn names [state]
  (vec (reduce #(conj %1 (:name %2)) #{} (:events @app-state))) )

(defn event-selected? [event selections]
  (some #(and (= (:style %1) (:style event)) (= (:substyle %1) (:substyle event)) (:selected %1)) selections)
  )
;  (if-let [selection (style-selection (:style event) selections)]
;    (if-some [substyle (:substyle event)]
;      (contains? (set (:substyles selection)) substyle)
;      true
;      )
;    )
;  )

(defn selected-names [events selections]
  (mapv :name (filterv #(event-selected? %1 selections) events))
  )

;(trace/trace-forms {:tracer trace/default-tracer}
(defn substyle-entry [owner name substyle selected]
  (let [cleanstyle {:style name :substyle substyle}]
    (dom/div #js {:className "checkbox"}
             (dom/label nil
                        (dom/input #js {:type "checkbox"
                                        :checked selected
                                        :onClick #(select-style owner cleanstyle (not selected))
                                        } substyle ))))
  )
;)

(defn style-header [owner name selected]
  (dom/div #js {:className "checkbox"}
           (dom/label nil
                      (dom/input #js {:type "checkbox" :checked selected :onClick #(select-style owner {:style name} (not selected))} name ))))

;(trace/trace-forms {:tracer trace/default-tracer}
(defn style-entry [owner name substyles selectors]
  (let [selection (style-selection name selectors)
        selected-substyles (set (map :substyle selection))]
    (apply p/panel {:header (style-header owner name (not (empty? selection))) ;(i/input {:style #js {:display :inline}, :type :checkbox, :label name})
                    :list-group (apply dom/ul #js {:className "list-group"}
                                       (map #(dom/li #js {:className "list-group-item"}
                                                     (substyle-entry owner name %1 (contains? selected-substyles %1))
                                                     ) substyles) )
                    }
           nil
           ) 
    )
  )
;)

(defn catalogue-view [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:styles (all-selections data)} )
    om/IRenderState
    (render-state [this state]
      (dom/div #js {:id "catalogue"}
               (dom/h2 nil "Dancing in Moscow")
               (g/grid {}
                       (dom/div #js {:className "row small-gutter"}
                              (g/col {:xs 4}
                                     (apply p/panel {:header "Style"}
                                            (map #(style-entry owner %1 (substyles data %1) (:styles state)) (styles data))
                                            ))

                              (g/col {:xs 4}
                                     (apply p/panel {:header "Name"}
                                            (map #(p/panel {} %1) (selected-names (:events data) (:styles state)))
                                            ))
                              )
                       )
             )
      )
    )
  )

(defn test-view [_ _]
  (reify
    om/IRender
    (render [_]
      (p/panel
        {:header "List group panel"
         :list-group (dom/ul #js {:className "list-group"}
                             (dom/li #js {:className "list-group-item"} "Item 1")
                             (dom/li #js {:className "list-group-item"} "Item 2")
                             (dom/li #js {:className "list-group-item"} "Item 3"))}
        nil)
      )
    )
  )

;(om/root test-view app-state
(om/root catalogue-view app-state
         {:target (. js/document (getElementById "catalogue"))})

(defn on-js-reload [] )
