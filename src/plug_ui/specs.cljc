(ns plug-ui.specs
  "Specs that are typically for internal use by UI components and related code.
  Note: Might move to more specific namespaces later"
  (:require [clojure.spec.alpha :as s]))


(s/def ::ttl (s/and integer? #(>= % 3000)))
(s/def ::epoch-millis (s/and integer? #(> % 1606903617095)))
(s/def ::severity #{:error :warn :info :undefined})
(s/def ::expires ::epoch-millis)
(s/def ::id integer?)
(s/def ::text (s/and string? not-empty))
(s/def ::notification-seed (s/keys :req-un [::text]
                                   :opt-un [::severity]))
(s/def ::notification (s/keys :req-un [::id ::severity ::text ::expires]))
(s/def ::notifications (s/coll-of ::notification))