package functional

import functional.sockets.MouseSocketsServer
import functional.sockets.VolumeChange
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

private const val HANDLE_VOLUME_EXE_PATH = "src/main/resources/nircmd.exe"
private const val CHANGE_VOLUME_COMMAND = "changesysvolume"

abstract class Action {

    abstract fun performAction(robot: Robot)

    class Click(private val action: MouseClick): Action() {
        override fun performAction(robot: Robot) {
            when(action){
                MouseClick.RIGHT -> {
                    robot.mousePress(MouseEvent.BUTTON3_DOWN_MASK)
                    robot.mouseRelease(MouseEvent.BUTTON3_DOWN_MASK)
                }
                MouseClick.LEFT -> {
                    robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK)
                    robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK)
                }
                MouseClick.LEFT_DOWN -> {
                    robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK)
                }
                MouseClick.LEFT_UP -> {
                    robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK)
                }
            }
        }
    }

    class MoveBy(private val x: Float, private val y: Float): Action() {
        override fun performAction(robot: Robot) {
            val actualPosition = MouseInfo.getPointerInfo().location
            val nextPosition = MouseSocketsServer.Point(actualPosition.x + x, actualPosition.y + y)
            robot.mouseMove(nextPosition.x.toInt(), nextPosition.y.toInt())
        }
    }

    class Text(private val text: String): Action() {
        override fun performAction(robot: Robot) {
            text.toCharArray().asIterable().forEach {
                val keyCode = KeyEvent.getExtendedKeyCodeForChar(it.code)
                if (KeyEvent.CHAR_UNDEFINED != it) {
                    if(Character.isUpperCase(it)) robot.keyPress(KeyEvent.SHIFT_DOWN_MASK)
                    robot.keyPress(keyCode)
                    robot.keyRelease(keyCode)
                    if(Character.isUpperCase(it)) robot.keyRelease(KeyEvent.SHIFT_DOWN_MASK)
                }
            }
        }
    }

    class Volume(private val change: VolumeChange): Action() {
        override fun performAction(robot: Robot) {
            Runtime.getRuntime().exec(arrayOf(
                HANDLE_VOLUME_EXE_PATH,
                CHANGE_VOLUME_COMMAND,
                change.units.toString()
            ))
        }

    }

    class Key(val keyCode: Int): Action(){
        override fun performAction(robot: Robot) {
            robot.keyPress(keyCode)
            robot.keyRelease(keyCode)
        }
    }

    class Empty(): Action(){
        override fun performAction(robot: Robot) {
            // nothing
        }
    }
}

enum class MouseClick(val msg: String) {
    RIGHT("right_click"),
    LEFT("left_click"),
    LEFT_DOWN("left_click_down"),
    LEFT_UP("left_click_up")
}