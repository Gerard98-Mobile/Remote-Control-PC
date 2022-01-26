package functional.sockets

import com.google.gson.annotations.SerializedName
import functional.Action

data class SocketMessage(
    @SerializedName("point")
    val point: MouseSocketsServer.Point?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("key")
    val keyCode: Int?
){
    constructor() : this(null,null,null)

    fun getAction() : Action {
        return when {
            point != null -> Action.MoveBy(point.x, point.y)
            text != null -> Action.Text(text)
            keyCode != null -> Action.Key(keyCode)
            else -> Action.Empty()
        }
    }


}