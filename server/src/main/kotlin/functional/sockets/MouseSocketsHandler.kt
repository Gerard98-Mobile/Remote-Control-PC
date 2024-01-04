package functional.sockets

abstract class MouseSocketsHandler {
    abstract fun onNewMessage(message: String)
    abstract suspend fun onError(error: Throwable)
}