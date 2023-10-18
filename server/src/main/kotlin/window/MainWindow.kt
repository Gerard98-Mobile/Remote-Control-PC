package window

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import common.LocalAppResources
import components.Tab
import kotlinx.coroutines.launch
import style.DefaultText
import style.LightGrey
import style.icMinimize
import style.icRemote

enum class LoggingTab {
    LOGS, ERRORS;
}

@Composable
@Preview
fun MainWindow(state: MainWindowState) {
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

        Card(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.White,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                var startOffset: Offset? = null

                Row(
                    modifier = Modifier.fillMaxWidth().pointerInput(Unit){
                        detectDragGestures(
                            onDrag = { change, _ ->
                                state.replaceWindow(change, startOffset ?: return@detectDragGestures)
                            },
                            onDragStart = {
                                startOffset = it
                            }
                        )
                    }.padding(10.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ){

                    Image(icMinimize(), null, Modifier.clickable {
                        hide()
                    }, alignment = Alignment.Center)

                    Spacer(Modifier.width(10.dp))

                    Image(Icons.Default.Close, null, Modifier.clickable {
                        exit()
                    })
                }

                Spacer(Modifier.height(15.dp))

                Image(
                    icRemote(),
                    contentDescription = null,
                    modifier = Modifier.width(70.dp)
                )

                Spacer(Modifier.height(15.dp))

                DefaultText(
                    state.userNetworkData,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                )

                DefaultText(
                    state.text
                )

                Spacer(Modifier.height(25.dp))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp)) {
                    LoggingTab.values().forEach {
                        Tab(it.name, if (state.selectedTab == it) LightGrey else Color.White, modifier = Modifier.weight(1f).fillMaxWidth()) {
                            state.selectedTab = it
                        }
                    }
                }

                TextList(
                    when (state.selectedTab) {
                        LoggingTab.LOGS -> state.logs
                        LoggingTab.ERRORS -> state.errorLogs
                    }
                )
            }
        }
    }
}


@Composable
fun TextList(data: List<String>) {
    val scrollState = rememberScrollState()
    Column(
        Modifier.background(LightGrey).fillMaxSize().padding(10.dp).verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        data.forEach {
            DefaultText(it)
        }
    }
}