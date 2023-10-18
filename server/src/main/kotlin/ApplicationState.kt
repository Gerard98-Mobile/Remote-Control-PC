import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import window.MainWindowState

@Composable
fun rememberApplicationState() = remember {
    ApplicationState().apply {
        newWindow()
    }
}

class ApplicationState {

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

    fun exit() {
        val windowsCopy = windows.reversed()
        for (window in windowsCopy) {
            if (!window.exit()) {
                break
            }
        }
    }
}