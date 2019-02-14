(ns pong.core
  (:require [quil.core :as q])
  (:import [java.awt.event KeyEvent]))


(defn draw-rect [r]
  (q/rect (:x r) (:y r) (:w r) (:h r)))
(def r-left (atom {:x 10 :y 65 :w 10 :h 70}))
(def r-right (atom {:x 430 :y 65 :w 10 :h 70}))
(def ball (atom {:x 225 :y 100 :w 10 :h 10}))
(def ball-dir (atom [1 0]))

; here is the easy way to do it:
(comment
  (defn next-ball [ball dir]
    (let [dx (first dir)
          dy (second dir)]
      (assoc ball :x (+ (:x ball) dx)
                  :y (+ (:y ball) dy))))

  )

(comment ; usage:
  (next-ball {:x 0 :y 0 :w 10 :h 10} [1 -1])
  ; => {:y -1, :x 1, :h 10, :w 10}
  )

; the more elegant way is by using destructuring:
(defn next-ball [b [dx dy]]
  (assoc b :x (+ (:x b) dx)
           :y (+ (:y b) dy)))


(defn hit-factor [r b]
  (- (/ (- (:y b) (:y r))
        (:h r))
     0.5))


;; determine if two rectangles intersect
(defn rect-intersects? [a b]
  (let [top-left-corner-a-x (:x a)
        top-left-corner-a-y (:y a)
        bottom-right-corner-a-x (+ (:x a) (:w a))
        bottom-right-corner-a-y (+ (:y a) (:h a))
        top-left-corner-b-x (:x b)
        top-left-corner-b-y (:y b)
        bottom-right-corner-b-x (+ (:x b) (:w b))
        bottom-right-corner-b-y (+ (:y b) (:h b))]
    (cond
      (and
        (<= top-left-corner-a-x bottom-right-corner-b-x)
        (>= bottom-right-corner-a-x top-left-corner-b-x)
        (<= top-left-corner-a-y bottom-right-corner-b-y)
        (>= bottom-right-corner-a-y top-left-corner-b-y))
      true
      :else
      false)))

(defn update-ball []
  (swap! ball next-ball @ball-dir)
  ; ball hit top or bottom border?
  (when (or (> (:y @ball) 200) (< (:y @ball) 0))
    ; invert y direction
    (swap! ball-dir (fn [[x y]] [x (- y)])))

  ; ball hit the left racket?
  (when (rect-intersects? @r-left @ball)
    (let [t (hit-factor @r-left @ball)]
      ; invert x direction, set y direction to hitfactor
      (swap! ball-dir (fn [[x _]] [(- x) t]))))

  ; ball hit the right racket?
  (when (rect-intersects? @r-right @ball)
    (let [t (hit-factor @r-right @ball)]
      ; invert x direction, set y direction to hitfactor
      (swap! ball-dir (fn [[x _]] [(- x) t])))))

(defn draw []
  ;set background
  (q/background 0x20)
  (q/fill 0xff)
  ;draw rackets
  (draw-rect @r-left)
  (draw-rect @r-right)
  (draw-rect @ball))


(def speed-multiplier 5)
(def inc-by-multiplier #(+ % speed-multiplier))
(def dec-by-multiplier #(- % speed-multiplier))

;input
(defn key-pressed []
  (cond
    ; left
    (= (q/key-code) KeyEvent/VK_W)
    (swap! r-left update-in [:y] dec-by-multiplier)
    (= (q/key-code) KeyEvent/VK_S)
    (swap! r-left update-in [:y] inc-by-multiplier)
    ; right
    (= (q/key-code) KeyEvent/VK_UP)
    (swap! r-right update-in [:y] dec-by-multiplier)
    (= (q/key-code) KeyEvent/VK_DOWN)
    (swap! r-right update-in [:y] inc-by-multiplier)))

;run
(q/defsketch pong
           :title "CSG 2D pong game"
           :size [450 200]
           :setup (fn [] (q/smooth) (q/no-stroke) (q/frame-rate 60))
           :draw (fn [] (update-ball) (draw))
           :key-pressed key-pressed)


