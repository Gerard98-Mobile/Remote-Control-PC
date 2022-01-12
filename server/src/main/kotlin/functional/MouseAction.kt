package functional

abstract class MouseAction {
    class Click(val action: MouseClick): MouseAction()
    class MoveBy(val x: Float, val y: Float): MouseAction()
}

enum class MouseClick(val msg: String) {
    RIGHT("right_click"), LEFT("left_click")
}