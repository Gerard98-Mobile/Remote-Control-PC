package pl.gg.client.ui.views.home

import android.util.Log
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.gg.client.Config
import pl.gg.client.ui.data.ConnectionState
import pl.gg.client.ui.functional.InterfaceScanner
import pl.gg.client.ui.functional.KeyboardKey
import pl.gg.client.ui.functional.SocketMessage
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger

class HomeViewModel(
    config: Config = Config
) : ViewModel() {

    companion object {
        private const val PORT = 6886
        private const val SEARCH_HOST_TIMEOUT_MS = 500
    }

    sealed class Event {
        class Snackbar(val message: String) : Event()
    }

    data class State(
        val loading: Boolean = false,
        val inetAddress: String?,
        val connection: ConnectionState = ConnectionState.DISCONNECTED,
        val networksAvailable: List<String> = emptyList(),
        val speed: Float,
        val isHostsDialogVisible: Boolean = false
    )

    private val _state = MutableStateFlow(State(
        inetAddress = config.serverInetAddress,
        speed = config.moveSpeed
    ))
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    init {
        checkHost()
    }

    fun updateState(action: State.() -> State) {
        _state.value = action.invoke(state.value)
    }

    fun updateSpeed(value: Float) = updateState {
        this.copy(speed = value)
    }

    private fun updateConnection(connected: Boolean) = updateState {
        this.copy(
            connection = if (connected) ConnectionState.CONNECTED else ConnectionState.DISCONNECTED
        )
    }

    private fun updateProgress(progress: Boolean) = updateState {
        this.copy(
            loading = progress
        )
    }

    fun putEvent(event: Event) = viewModelScope.launch {
        _events.emit(event)
    }

    fun updateConfigMoveSpeed() {
        Config.moveSpeed = state.value.speed
    }

    private fun checkHost() = state.value.inetAddress?.let {
        isHostReachable(it) {
            updateConnection(it)
        }
    }

    fun searchForHosts() {
        updateProgress(true)
        val resultCount = AtomicInteger()
        val availableHosts = mutableListOf<String>()
        val clientNetwork = InterfaceScanner.getNetworkInterfaces().getOrNull(0) ?: return
        val ipPattern = clientNetwork.address.hostAddress?.dropLastWhile { it != '.' } ?: return

        for (i in 1..252) {
            val host = "${ipPattern}${i}"
            isHostReachable(host) {
                if (it) availableHosts.add(host)
                if (resultCount.incrementAndGet() >= 252) {
                    updateState {
                        this.copy(
                            loading = false,
                            networksAvailable = availableHosts,
                            isHostsDialogVisible = availableHosts.isNotEmpty()
                        )
                    }
                    if (availableHosts.isEmpty()) putEvent(Event.Snackbar("There is no hosts available"))
                }
            }
        }
    }

    private fun isHostReachable(address: String, callback: (Boolean) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val socket = Socket()
                val inetSocketAddress = InetSocketAddress(address, PORT)
                socket.connect(inetSocketAddress, SEARCH_HOST_TIMEOUT_MS)
                socket.close()
                callback.invoke(true)
            } catch (e: IOException) {
                callback.invoke(false)
            }
        }

    @OptIn(ExperimentalComposeUiApi::class)
    fun sendMsgByKeyEvent(event: KeyEvent) : Boolean =
        when (event.key) {
            Key.Backspace -> {
                sendMsg(SocketMessage.KeyMessage(KeyboardKey.BACKSPACE))
                true
            }
            Key.Spacebar -> {
                sendMsg(SocketMessage.KeyMessage(KeyboardKey.SPACEBAR))
                true
            }
//            @TODO add volume changes
//            Key.VolumeUp -> {
//                sendMsg(SocketMessage.VolumeMessage("UP"))
//                true
//            }
//            Key.VolumeDown -> {
//                sendMsg(SocketMessage.VolumeMessage("DOWN"))
//                true
//            }
            else -> false
        }


    fun sendMsg(message: SocketMessage) = viewModelScope.launch(Dispatchers.IO) {
        if (state.value.connection != ConnectionState.CONNECTED) return@launch

        try {
            Log.v("SocketManager", "send msg ${message.getMsg()}")
            val socket = Socket(state.value.inetAddress, PORT)
            val output = DataOutputStream(socket.getOutputStream())
            val data = message.getMsg()
            output.writeUTF(data)
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}