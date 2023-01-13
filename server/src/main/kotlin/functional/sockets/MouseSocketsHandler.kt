package functional.sockets

abstract class MouseSocketsHandler {
    abstract fun onNewMessage(message: String)
    abstract fun onError(error: Throwable)
}