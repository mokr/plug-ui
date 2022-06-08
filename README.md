# plug-ui

Reusable UI components for Clojurescript + Reagent + re-frame projects.

Typically used in a [Luminus](https://luminusweb.com) based project
utilizing [shadow-cljs](https://github.com/thheller/shadow-cljs)

# Disclaimer

This library should be considered an experiment in code reuse and boilerplate reduction.

Use at your own risk! Breakage is to be expected as:

* Library is gradually tuned for my typical use cases.
* I improve on my general library development.

# Usage

In Luminus project's core.cljs

```clojure
(ns foo.core
  (:require [plug-ui.bulma.notifications :as notifications])),,,

(defn page []
  (when-let [page @(rf/subscribe [:common/page])]
    [:div
     [notifications/panel :style {:top "50px"}] ;; << Add this. The :style part is optional, but allows adjusting placement
     [navbar]
     [page]]))

```

## License

Copyright © 2022 Morten Kristoffersen

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
