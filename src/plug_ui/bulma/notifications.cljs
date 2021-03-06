(ns plug-ui.bulma.notifications
  "GUI panel handling notifications. Typically seen top right in UI"
  (:require
    [clojure.spec.alpha :as s]
    [plug-ui.specs :as $]
    [plug-utils.maps :as um]
    [plug-utils.re-frame :refer [<sub >evt]]
    [plug-utils.spec :refer [valid?]]
    [plug-utils.time :as ut]
    [re-frame.core :as rf]
    [taoensso.timbre :as log]))

;;TODO: Allow user to configure ttl and max-_ vars in definitions below. E.g. as args to panel
;;TODO: Create a notifications center component where user can bring up notification history

;;-------------------------------------------------
;; DEFINITIONS

(def ^:private notifications-key ::cache)                   ;; Where we store this in app-db
(def ^:private ttl 5000)                                    ;; How long before notifications are automatically dismissed
(def ^:private max-presented 6)                             ;; How many of the registered notifications to present at any one time
(def ^:private notification-defaults {:severity :undefined})

;;-------------------------------------------------
;; HELPERS

(defn- expired?
  "Is the value of :expired key less than the provided epoch-millis value?"
  [time-now notification]
  (-> notification :expires (< time-now)))


(defn- remove-expired-notifications
  "Given a list of notifications and a timestamp (in epoch-millis),
  remove the ones that are expired."
  [notifications time-now]
  (->> notifications
       (remove (partial expired? time-now))))


(defn- make-notification
  "Create a notification from the provided seed map"
  [seed time-now ttl]
  {:pre  [(valid? ::$/notification-seed seed) (valid? ::$/epoch-millis time-now) (valid? ::$/ttl ttl)]
   :post [(valid? ::$/notification %)]}
  (-> (merge notification-defaults seed)
      (assoc :id (rand-int 1e6))
      (um/some-updates
        :created #(ut/time-now-local-str)
        :expires #(+ time-now ttl))))


(defn- remove-by-id
  "Return list without the entry with the given ID"
  [id notifications]
  (remove #(-> % :id (= id)) notifications))


;;-------------------------------------------------
;; PUBLIC EVENTS

;;TODO: Allow :ttl and/or :ttl-ms as part of seed
(rf/reg-event-fx
  :new/notification
  [rf/trim-v]
  (fn [{:keys [db]} [seed]]
    {:pre [(valid? ::$/notification-seed seed)]}
    (let [time-now         (ut/epoch-millis-now)
          new-notification (make-notification seed time-now ttl)
          cleanup-timer    (inc ttl)]                       ;; Ensure time has expired when scheduled clean event fires
      {:db (update db notifications-key conj new-notification)
       :fx [[:dispatch-later {:ms cleanup-timer :dispatch [::clean-expired-notifications]}]]})))


;;-------------------------------------------------
;; EVENTS

(rf/reg-event-db
  ::clean-expired-notifications
  (fn [db _]
    (let [time-now (ut/epoch-millis-now)]
      (update db notifications-key remove-expired-notifications time-now))))


(rf/reg-event-db
  ::close-notification
  [rf/trim-v]
  (fn [db [id]]
    (update db notifications-key (partial remove-by-id id))))


;;-------------------------------------------------
;; SUBS

(rf/reg-sub
  ::active-notifications                                    ;; Using current NS as this is only assumed to be used by UI below
  (fn [db _]
    (get db notifications-key)))


;;-------------------------------------------------
;; DISPLAY

(def ^:private severity->subclass
  {:info  "info"
   :warn  "warning"
   :error "danger"})


(defn- prep-for-display
  "Add some display related keys to notification"
  [{:keys [severity] :as notification}]
  {:pre [(valid? ::$/notification notification)]}
  (let [subclass (or (severity->subclass severity) "dark")
        class    (str "is-" subclass)]
    (assoc notification
      :class class)))


(rf/reg-sub
  ::ui-prepared-notifications
  :<- [::active-notifications]
  (fn [notifications]
    (->> notifications
         (map prep-for-display)
         (sort-by :expires)
         (reverse)                                          ;; Present the last..
         (take max-presented)                               ;; x..
         (reverse))))                                       ;; in chronological order


;;-------------------------------------------------
;; UI

; Note: Inline style to avoid lib users needing extra CSS
(defn- notification-message [{:keys [created text class id] :as notification}]
  {:pre [(valid? ::$/notification notification)]}
  [:div.notification.m-1 {:class [class "is-light"]}
   [:button.delete.is-info {:on-click #(>evt [::close-notification id])}]
   [:div>small>em created]
   text])


; Note: Inline style to avoid lib users needing extra CSS
(defn panel
  "Panel that will ensure that notifications pops up top right if places correctly withing page"
  [& {:keys [style] :as overrides}]
  [:div#notifications-panel
   {:style (merge {:z-index   9999
                   :position  "fixed"
                   :top       "52px"                        ; Typically right below navbar
                   :right     0
                   :max-width "40%"}
                  style)}
   (for [notification (<sub [::ui-prepared-notifications])]
     ^{:key (:id notification)}
     [notification-message notification])])
