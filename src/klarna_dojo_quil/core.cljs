(ns klarna-dojo-quil.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def pressed-key (atom nil))

(def keycode-left 37)
(def keycode-right 39)

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :rgb)
  (q/stroke 0 0 0)

  (.addEventListener js/window
                     "keydown"
                     (fn [ev] (reset! pressed-key (.-keyCode ev))))
  (.addEventListener js/window
                     "keyup"
                     (fn [_] (reset! pressed-key nil)))

  {:paddle {
    :x 170
    :y 485}
   :ball
   {:x 70
    :y 100
    :x-delta 1
    :y-delta 1}})

(defn draw [state]
  (q/background 210 210 210)
  (q/fill 0 0 0)
  (q/rect (get-in state [:paddle :x])
          (get-in state [:paddle :y])
          100
          15)
  (q/fill 200 0 0)
  (q/ellipse (get-in state [:ball :x])
             (get-in state [:ball :y])
             10
             10))

(defn move-paddle [state]
  (cond
    (= @pressed-key keycode-right)
    (update-in state [:paddle :x] + 5)

    (= @pressed-key keycode-left)
    (update-in state [:paddle :x] - 5)

    :default
    state))

(defn move-ball [state]
  (-> state
    (update-in [:ball :x] + (get-in state [:ball :x-delta]))
    (update-in [:ball :y] + (get-in state [:ball :y-delta]))))

(defn bounce-ball [state]
 (let [ball (:ball state)
       x (:x ball)
       y (:y ball)
       dx (:x-delta ball)
       dy (:y-delta ball)]
       (cond
         (< x 0)
         (update-in state [:ball :x-delta] (fn [_] 1))

         (> x 500)
         (update-in state [:ball :x-delta] (fn [_] -1))

         (< y 0)
         (update-in state [:ball :y-delta] (fn [_] 1))

         (> y 500)
         (update-in state [:ball :y-delta] (fn [_] -1))



         :default
         state)))

(defn update-state [state]
  (-> state
     move-ball
     bounce-ball
     move-paddle))

(q/defsketch klarna-dojo-quil
  :host "klarna-dojo-quil"
  :size [500 500]
  :setup setup
  :draw draw
  :middleware [m/fun-mode]
  :update update-state)
