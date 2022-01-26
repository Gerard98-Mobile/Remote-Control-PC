package pl.gg.client.ui.functional

import pl.gg.client.Config

abstract class SocketMessage {

    class KeyMessage(val keyboardKey: KeyboardKey) : SocketMessage() {
        override fun getMsg(): String {
            return "{'key':${keyboardKey.javaKeyCode}}"
        }
    }

    class Text(val text: String) : SocketMessage() {
        override fun getMsg(): String {
            return "{'text':${text}}"
        }
    }

    class Click(val method: ClickMethod) : SocketMessage(){
        override fun getMsg(): String {
            return method.msg
        }
    }

    class MoveBy(val x: Float, val y: Float, val speed: Float = Config.moveSpeed) : SocketMessage(){
        override fun getMsg(): String {
            return "{'point':{'x':${x * speed}, 'y':${y * speed}}}"
        }
    }

    abstract fun getMsg() : String
}

enum class ClickMethod(val msg: String){
    RIGHT("right_click"), LEFT("left_click"),
    LEFT_DOWN("left_click_down"), LEFT_UP("left_click_up")
}

enum class KeyboardKey(val javaKeyCode: Int){
    BACKSPACE(8), SPACEBAR(32)
}