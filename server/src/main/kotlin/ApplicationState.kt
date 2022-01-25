import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import common.Settings
import window.MainWindowState

@Composable
fun rememberApplicationState() = remember {
    ApplicationState().apply {
        newWindow()
    }
}

class ApplicationState {
    val settings = Settings()
    val tray = TrayState()

    private val _windows = mutableStateListOf<MainWindowState>()
    val windows: List<MainWindowState> get() = _windows

    fun newWindow() {
        _windows.add(
            MainWindowState(
                application = this,
                exit = _windows::remove
            )
        )
    }

    fun sendNotification(notification: Notification) {
        tray.sendNotification(notification)
    }

    suspend fun exit() {
        val windowsCopy = windows.reversed()
        for (window in windowsCopy) {
            if (!window.exit()) {
                break
            }
        }
    }
}