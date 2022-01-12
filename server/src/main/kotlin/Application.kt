import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.MenuScope
import androidx.compose.ui.window.Tray
import common.LocalAppResources
import kotlinx.coroutines.launch
import window.NotepadWindow

@Composable
fun ApplicationScope.NotepadApplication(state: NotepadApplicationState) {

    for (window in state.windows) {
        key(window) {
            NotepadWindow(window)
        }
    }
}


@Composable
private fun MenuScope.ApplicationMenu(state: NotepadApplicationState) {
    val scope = rememberCoroutineScope()
    fun exit() = scope.launch { state.exit() }

    Item("New", onClick = state::newWindow)
    Separator()
    Item("Exit", onClick = { exit() })
}