(ns scdm.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [om-bootstrap.grid :as g]
            [om-bootstrap.panel :as p]
            [om-bootstrap.input :as i]
            ))

(enable-console-print!)

(def app-state
  (atom
    {:events
     [{:style "Hustle", :substyle "Social", :type :disco,  :name "Teplica",  :timetable [[:tue "20:30" "23:30"] [:sat "20:30" "00:30"]]}
      {:style "Hustle", :substyle "Sport",  :type :disco,  :name "Mossovet", :timetable [[:fri "20:30" "23:30"] [:sun "20:00" "00:00"]]}
      {:style "WCS",                       :type :disco,  :name "Ivara",    :timetable [[:sat "20:30" "23:30"]]}
      {:style "WCS",                       :type :course, :name "Lisoborie",:timetable [[:mon "20:30" "22:30"] [:thu "20:30" "22:30"]]}
      ]
     }
    )
  )

(defn styles [state]
  (vec (reduce #(conj %1 (:style %2)) #{} (:events @app-state))) )

(defn substyles [state style]
  (vec (for [e (:events @app-state),
             :when (= (:style e) style),
             :when (:substyle e)]
         (:substyle e) )))

(defn names [state]
  (vec (reduce #(conj %1 (:name %2)) #{} (:events @app-state))) )


(defn substyle-entry [substyle]
  (dom/div #js {:className "checkbox"}
           (dom/label nil
                      (dom/input #js {:type "checkbox"} substyle ))))
  ;(i/input {:type :checkbox, :label substyle}) )

(defn style-header [name]
  (dom/div #js {:className "checkbox"}
           (dom/label nil
                      (dom/input #js {:type "checkbox"} name ))))

(defn style-entry [name substyles]
  (apply p/panel {:header (style-header name) ;(i/input {:style #js {:display :inline}, :type :checkbox, :label name})
                  :list-group (apply dom/ul #js {:className "list-group"}
                                     (map #(dom/li #js {:className "list-group-item"} (substyle-entry %1)) substyles) )
                  }
         nil
         ) )

(defn catalogue-view [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:id "catalogue"}
               (dom/h2 nil "Dancing in Moscow")
               (g/grid {}
                       (g/row {}
                              (g/col {:xs 4}
                                     (apply p/panel {:header "Style"}
                                            (map #(style-entry %1 (substyles data %1)) (styles data))
                                            ))

                              (g/col {:xs 4}
                                     (apply p/panel {:header "Name"}
                                            (map #(p/panel {} %1) (names data))
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
