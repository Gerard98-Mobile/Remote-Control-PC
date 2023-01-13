package window

import ApplicationState
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import functional.sockets.MouseSocketsHandler
import functional.sockets.MouseSocketsServer
import kotlinx.coroutines.*
import java.net.Inet4Address
import java.net.NetworkInterface

data class NetworkData(
    val id: String?,
    val name: String?
)

class MainWindowState(
    private val application: ApplicationState,
    private val exit: (MainWindowState) -> Unit
) {

    val window = WindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        size = DpSize(300.dp, 500.dp)
    )

    private var _text by mutableStateOf("")
    var text: String
        get() = _text
        set(value) {
            _text = value
        }

    private var _logs by mutableStateOf(listOf<String>())
    val logs: List<String>
        get() = _logs

    fun addLog(value: String) {
        _logs.toMutableList().apply {
            add(0, value)
            if (size > 20) removeLast()
            _logs = this
        }
    }

    private var _userNetworkData by mutableStateOf("N/D")
    var userNetworkData
        get() = _userNetworkData
        set(value) {
            _userNetworkData = value
        }

    private val mouseSocketServer = MouseSocketsServer(
        object : MouseSocketsHandler(){
            override fun onNewMessage(message: String) {
                addLog(message)
            }

            override fun onError(error: Throwable) {
                text = error.message ?: "Unknown error"
            }
        }
    )

    fun replaceWindow(change: PointerInputChange, offset: Offset){
        val actualX = window.position.x
        val actualY = window.position.y
        val changeX = actualX + (change.position.x).dp
        val changeY = actualY + (change.position.y).dp
        val realX = changeX - offset.x.dp
        val realY = changeY - offset.y.dp
        window.position = WindowPosition(realX, realY)
    }


    private fun fetchNetworkData() : List<NetworkData> {
        val items = mutableListOf<NetworkData>()
        val iterator = NetworkInterface.getNetworkInterfaces().asIterator()

        while(iterator.hasNext()){
            val nf = iterator.next()
            nf.inetAddresses.toList().forEach {
                if(!it.isLoopbackAddress && it is Inet4Address) {
                    items.add(NetworkData(it.hostAddress, it.hostName))
                }
            }
        }
        return items
    }

    suspend fun exit(): Boolean {
        exit(this)
        return true
    }

    suspend fun hide() {
        window.isMinimized = true
    }

    suspend fun startServer() = withContext(Dispatchers.IO) {
        text = "Starting Server..."
        val userNetwork = fetchNetworkData()
        val serverInetAddress = if((userNetwork?.size ?: 0) > 0) userNetwork?.get(0)?.id else null
        userNetworkData = serverInetAddress ?: "No network data"

        text = "Server running"
        addLog("Waiting for messages...")
        mouseSocketServer.runServer()
    }

    suspend fun sendTestMessage() {
        mouseSocketServer.sendTestMessage(userNetworkData)
    }

}