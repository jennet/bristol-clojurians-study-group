# bristol-clojurians-study-group

## Weds Feb 13th

2D pong game using Quil

Notes

* [Event details](https://www.meetup.com/Bristol-Clojurians/events/nwvqlqyzdbrb/)
* [Pong game](https://noobtuts.com/clojure/2d-pong-game)
* [Quil docs](http://quil.info/api)

Started a new project with `lein new quil pong` and had a quick look at the animation before deleting the auto-generated code to follow the pong tutorial

Was unable to complete the tutorial at first due to the `rect-intersects` function being behind a paywall

`rect-intersects`:

```


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

```

Paddles were _really_ slow, so amended `dec`/`inc` to use a multiplier:

```

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
```