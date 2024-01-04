import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.MenuScope
import kotlinx.coroutines.launch
import window.MainWindow

@Composable
fun ApplicationScope.Application(state: ApplicationState) {

    for (window in state.windows) {
        key(window) {
            MainWindow(window)
        }
    }
}


@Composable
private fun MenuScope.ApplicationMenu(state: ApplicationState) {
    val scope = rememberCoroutineScope()
    fun exit() = scope.launch { state.exit() }

    Item("New", onClick = state::newWindow)
    Separator()
    Item("Exit", onClick = { exit() })
}