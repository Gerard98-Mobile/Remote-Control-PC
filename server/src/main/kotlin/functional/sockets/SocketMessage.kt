package functional.sockets

import com.google.gson.annotations.SerializedName
import functional.Action

enum class VolumeChange {
    UP, DOWN
}

data class SocketMessage(
    @SerializedName("point")
    val point: MouseSocketsServer.Point? = null,
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("key")
    val keyCode: Int? = null,
    @SerializedName("volume")
    val volume: VolumeChange? = null
){

    fun getAction() : Action {
        return when {
            point != null -> Action.MoveBy(point.x, point.y)
            text != null -> Action.Text(text)
            keyCode != null -> Action.Key(keyCode)
            volume != null -> Action.Volume(volume)
            else -> Action.Empty()
        }
    }


}