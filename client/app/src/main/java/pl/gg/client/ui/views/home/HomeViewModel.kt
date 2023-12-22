package pl.gg.client.ui.views.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
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

class HomeViewModel : ViewModel() {

    var data by mutableStateOf<List<InterfaceScanner.NetworkResult>>(emptyList())
    private var port = 6886

    var state by mutableStateOf(ConnectionState.DISCONNECTED)
    var isHostReachable by mutableStateOf(false)

    private var _inetServerAddress by mutableStateOf(Config.serverInetAddress)
    var inetServerAddress
        get() = _inetServerAddress
        set(value) {
            Config.serverInetAddress = value
            _inetServerAddress = value
            checkHost(value)
        }

    var progress by mutableStateOf(false)
    var hostsDialogVisibility by mutableStateOf(false)
    var availableHosts by mutableStateOf<List<String>>(emptyList())

    init {
        data = InterfaceScanner.getNetworkInterfaces()
        checkHost()
    }

    fun checkHost(
        inetHostAddress: String? = inetServerAddress,
        callback: (Boolean) -> Unit = { serverReachableChange(it) }
    ) {
        inetHostAddress?.let {
            isHostReachable(it) {
                callback.invoke(it)
            }
        }
    }

    fun searchForHosts(callback: (List<String>) -> Unit) {
        val resultCount = AtomicInteger()
        val availableHosts = mutableListOf<String>()
        val clientNetwork = InterfaceScanner.getNetworkInterfaces().getOrNull(0) ?: return
        val ipPattern = clientNetwork.address.hostAddress?.dropLastWhile { it != '.' } ?: return

        for (i in 1..252) {
            val host = "${ipPattern}${i}"
            isHostReachable(host) {
                if (it) availableHosts.add(host)
                if (resultCount.incrementAndGet() >= 252) {
                    callback.invoke(availableHosts)
                    resultCount.set(0)
                }
            }
        }
    }

    fun showHostDialog(hosts: List<String> = availableHosts) {
        availableHosts = hosts
        hostsDialogVisibility = true
    }

    private fun serverReachableChange(value: Boolean) = viewModelScope.launch(Dispatchers.Main) {
        isHostReachable = value
        state = if (value) ConnectionState.CONNECTED else ConnectionState.DISCONNECTED
    }

    private fun isHostReachable(address: String, callback: (Boolean) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val socket = Socket()
                val inetSocketAddress = InetSocketAddress(address, port)
                socket.connect(inetSocketAddress, 2000)
                socket.close()
                callback.invoke(true)
            } catch (e: IOException) {
                //e.printStackTrace()
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
        if (state != ConnectionState.CONNECTED) return@launch

        try {
            Log.v("SocketManager", "send msg ${message.getMsg()}")
            val socket = Socket(inetServerAddress, port)
            val output = DataOutputStream(socket.getOutputStream())
            val data = message.getMsg()
            output.writeUTF(data)
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}