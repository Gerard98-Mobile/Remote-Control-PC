package functional.sockets

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import functional.Action
import functional.MouseClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.impl.Log
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket

class MouseSocketsServer(
    private val handler : MouseSocketsHandler
) {

    private val port = 6886
    private val server = ServerSocket(port)

    suspend fun runServer() = withContext(Dispatchers.IO) {
        Log.info("Server: " + server.inetAddress.toString() + " " + server.localSocketAddress)
        while(true){
            Log.info("Waiting for message")
            this.runCatching {
                val socket = server.accept()
                val data = DataInputStream(BufferedInputStream(socket.getInputStream()))
                val message = data.readUTF()
                Log.info("Message appear: $message")
                handleMessage(message)
                handler.onNewMessage(message)
                message
            }.apply {
                if (this.isSuccess) return@apply

                val error = this.exceptionOrNull()
                Log.error(error?.message ?: "Socket Error")
                handler.onError(error ?: UnknownError())
            }
        }
    }

    data class Point(
        @SerializedName("x")
        val x: Float,
        @SerializedName("y")
        val y: Float
    ) {
        constructor() : this(0f,0f)
    }

    private val robot = Robot()
    private val gson = Gson()
    private fun handleMessage(data: String) {
        val action = when(data){
            MouseClick.LEFT.msg -> Action.Click(MouseClick.LEFT)
            MouseClick.LEFT_UP.msg -> Action.Click(MouseClick.LEFT_UP)
            MouseClick.LEFT_DOWN.msg -> Action.Click(MouseClick.LEFT_DOWN)
            MouseClick.RIGHT.msg -> Action.Click(MouseClick.RIGHT)
            else -> {
                val message = gson.fromJson<SocketMessage>(data, object : TypeToken<SocketMessage>(){}.type)
                message.getAction()
            }
        }
        action.performAction(robot)
    }

    var count = 0

    suspend fun sendTestMessage(serverInetAddress: String) = withContext(Dispatchers.IO) {
        try{
            val socket = Socket(serverInetAddress, port)
            val output = DataOutputStream(socket.getOutputStream())
            val data = "Test ${count++}"
            output.writeUTF(data)
        } catch (e: Exception){
            Log.error(e.message ?: "Send Message Error")
        }
    }

}