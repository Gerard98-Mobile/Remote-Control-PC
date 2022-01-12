package window

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import common.LocalAppResources
import kotlinx.coroutines.launch
import style.DefaultText
import style.icMinimize
import style.icRemote

@Composable
@Preview
fun NotepadWindow(state: NotepadWindowState) {
    val scope = rememberCoroutineScope()

    fun exit() = scope.launch { state.exit() }
    fun hide() = scope.launch { state.hide() }

    Window(
        state = state.window,
        title = "Server",
        icon = LocalAppResources.current.icon,
        onCloseRequest = { exit() },
        resizable = false,
        transparent = true,
        undecorated = true,
        focusable = true
    ) {
        LaunchedEffect(Unit) { state.startServer() }
//        WindowMenuBar(state)

        Card(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.White,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ){

                    Image(icMinimize(), null, Modifier.clickable {
                        hide()
                    }, alignment = Alignment.Center)

                    Spacer(Modifier.width(10.dp))

                    Image(Icons.Default.Close, null, Modifier.clickable {
                        exit()
                    })
                }

                Spacer(Modifier.height(55.dp))

                Image(
                    icRemote(),
                    contentDescription = null,
                    modifier = Modifier.width(150.dp)
                )

                Spacer(Modifier.height(75.dp))

                DefaultText(
                    state.userNetworkData,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                )

                DefaultText(
                    state.text
                )
            }
        }


    }
}

@Composable
private fun FrameWindowScope.WindowMenuBar(state: NotepadWindowState) = MenuBar {
    val scope = rememberCoroutineScope()

    fun startServer() = scope.launch { state.startServer() }
    fun sendTestMessage() = scope.launch { state.sendTestMessage() }
    fun exit() = scope.launch { state.exit() }

    Menu("Options") {
        Item("Send Test Message ", onClick = { sendTestMessage() })
        Separator()
        Item("Exit", onClick = { exit() })
    }
}