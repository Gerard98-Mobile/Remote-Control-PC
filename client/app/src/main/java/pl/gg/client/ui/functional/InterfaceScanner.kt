package pl.gg.client.ui.functional

import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException


object InterfaceScanner {
    fun getNetworkInterfaces(): List<NetworkResult> {
        return try {
            NetworkInterface.getNetworkInterfaces().asSequence().flatMap { networkInterface ->
                networkInterface.interfaceAddresses.asSequence().map {
                    val addr = it.address
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        NetworkResult(addr, networkInterface.displayName)
                    } else {
                        null
                    }
                }.filterNotNull()
            }.toList()
        } catch (ex: SocketException) {
            ex.printStackTrace()
            listOf()
        }
    }

    data class NetworkResult(
        val address: Inet4Address,
        val name: String
    )

}