package pl.gg.client.ui.functional

import pl.gg.client.Config

abstract class Motion {
    class Click(val method: ClickMethod) : Motion(){
        override fun getMsg(): String {
            return method.msg
        }
    }

    class MoveBy(val x: Float, val y: Float, val speed: Float = Config.moveSpeed) : Motion(){
        override fun getMsg(): String {
            return "{'x':${x * speed}, 'y':${y * speed}}"
        }
    }

    abstract fun getMsg() : String
}

enum class ClickMethod(val msg: String){
    RIGHT("right_click"), LEFT("left_click"),
    RIGHT_DOWN("right_click_down"), RIGHT_UP("right_click_up"),
    LEFT_DOWN("left_click_down"), LEFT_UP("left_click_up")

}