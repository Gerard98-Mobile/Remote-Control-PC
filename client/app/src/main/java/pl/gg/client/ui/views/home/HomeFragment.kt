package pl.gg.client.ui.views.home

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.gg.client.Config
import pl.gg.client.R
import pl.gg.client.ui.BoldText
import pl.gg.client.ui.Title
import pl.gg.client.ui.base.OutlineButton
import pl.gg.client.ui.components.FullscreenProgressIndicator
import pl.gg.client.ui.functional.*
import pl.gg.client.ui.theme.*
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Preview
@Composable
fun Home(
    viewModel: HomeViewModel = viewModel(),
    state: HomeState = rememberHomeState(if (viewModel.inetServerAddress.isNullOrEmpty()) BackdropValue.Revealed else BackdropValue.Concealed)
) {

    ClieantSideTheme {
        Box(modifier = Modifier.fillMaxSize()) {

            BackdropScaffold(
                scaffoldState = state.scaffold,
                gesturesEnabled = false,
                appBar = {
                    HomeAppBar(state = state.scaffold)
                },
                backLayerContent = {
                    HomeBackLayerContent(state)
                },
                frontLayerContent = {
                    HomeFrontLayer()
                })

            if (viewModel.progress) {
                FullscreenProgressIndicator()
            }
            if (viewModel.hostsDialogVisibility) {
                HostsDialog()
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun HomeBackLayerContent(
    state: HomeState,
    viewModel: HomeViewModel = viewModel()
) {

    Column(modifier = Modifier) {

        var speed by remember { mutableStateOf(Config.moveSpeed) }
        var newHost by remember { mutableStateOf("") }

        val colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.White,
            activeTickColor = Color.White
        )

        val outlineColors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            cursorColor = Color.White,
            placeholderColor = Color.White
        )

        if ((!viewModel.isHostReachable || !viewModel.connected) && !viewModel.inetServerAddress.isNullOrEmpty()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlineButton(text = "Try to reconnect", textColor = DarkOrange) {
                    viewModel.progress = true
                    viewModel.checkHost {
                        if (!it) state.showSnackbar("Host is not reachable")
                        viewModel.progress = false
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            OutlineButton(text = "Search hosts", textColor = Hyperlink, icon = R.drawable.ic_wifi, iconTint = Hyperlink) {
                viewModel.progress = true

                viewModel.searchForHosts {
                    viewModel.progress = false
                    if (it.isEmpty()) {
                        state.showSnackbar("There is no hosts available")
                        return@searchForHosts
                    }
                    viewModel.showHostDialog(it)
                }
            }
        }


        Title(
            name = "Mouse speed: x${speed.toInt()}",
            fontSize = 14.sp,
            modifier = Modifier.padding(10.dp)
        )
        Slider(value = speed,
            valueRange = 1f..5f,
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            steps = 3,
            onValueChange = {
                speed = it
            }, onValueChangeFinished = {
                Config.moveSpeed = speed
            },
            colors = colors
        )
    }

}

@ExperimentalMaterialApi
@Composable
fun HomeAppBar(viewModel: HomeViewModel = viewModel(), state: BackdropScaffoldState) {

    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .padding(10.dp)
            .height(IntrinsicSize.Min)
    ) {

        Image(
            painterResource(id = R.drawable.ic_remote), null, modifier = Modifier
                .fillMaxHeight()
                .padding(5.dp), colorFilter = ColorFilter.tint(Color.White)
        )

        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxHeight()
                .weight(1f), verticalArrangement = Arrangement.Center
        ) {
            if (!viewModel.inetServerAddress.isNullOrEmpty()) BoldText(
                viewModel.inetServerAddress ?: ""
            ) else BoldText("No host")
            if (viewModel.connected) Text("Connected", color = DarkGreen)
            else Text("Disconnected", color = DarkRed)
        }

        Image(painterResource(id = R.drawable.ic_settings),
            null,
            alignment = Alignment.TopEnd,
            modifier = Modifier
                .height(25.dp)
                .clickable {
                    scope.launch {
                        if (state.isRevealed) {
                            state.conceal()
                        } else {
                            state.reveal()
                        }
                    }

                },
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun HomeFrontLayer(viewModel: HomeViewModel = viewModel()) {

    val focusManager = LocalFocusManager.current
    val focusRequester = FocusRequester.Default
    var isFocused by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .alpha(if (viewModel.connected) 1f else 0.5f)
            .padding(top = 10.dp)
    ) {

        var start = 0f to 0f
        var lastMove = 0f to 0f

        var lastClickTime: Long = 0
        var clicking = false



        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp, bottom = 20.dp)
        ) {

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(if (isFocused) "Hide Keyboard" else "Show Keyboard", modifier = Modifier
                    .wrapContentHeight()
                    .clickable {
                        if (isFocused) focusManager.clearFocus(true) else focusRequester.requestFocus()
                    }
                    .padding(10.dp), textAlign = TextAlign.End)
            }

            TextField(value = "",
                keyboardActions = KeyboardActions(onPrevious = {
                    viewModel.sendMsg(SocketMessage.KeyMessage(KeyboardKey.BACKSPACE))
                }, onDone = {
                    viewModel.sendMsg(SocketMessage.KeyMessage(KeyboardKey.ENTER))
                    focusManager.clearFocus(true)
                }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                onValueChange = {
                    if (it == " ") viewModel.sendMsg(SocketMessage.KeyMessage(KeyboardKey.SPACEBAR))
                    else viewModel.sendMsg(SocketMessage.Text(it))
                }, modifier = Modifier
                    .alpha(0f)
                    .height(0.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        isFocused = it.isFocused
                    }
                    .clearFocusOnKeyboardDismiss()
                    .onKeyEvent {
                        when (it.key) {
                            Key.Backspace -> {
                                viewModel.sendMsg(SocketMessage.KeyMessage(KeyboardKey.BACKSPACE))
                                true
                            }
                            Key.Spacebar -> {
                                viewModel.sendMsg(SocketMessage.KeyMessage(KeyboardKey.SPACEBAR))
                                true
                            }
                            else -> false
                        }
                    })


            Card(backgroundColor = Color.Gray, modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {
                            start = it.rawX to it.rawY
                            lastMove = it.rawX to it.rawY
                            if (lastClickTime + 200 > System.currentTimeMillis()) {
                                clicking = true
                                viewModel.sendMsg(SocketMessage.Click(ClickMethod.LEFT_DOWN))
                            }
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val move = it.rawX - lastMove.first to it.rawY - lastMove.second
                            lastMove = it.rawX to it.rawY
                            viewModel.sendMsg(SocketMessage.MoveBy(move.first, move.second))
                        }
                        MotionEvent.ACTION_UP -> {
                            if (clicking) {
                                clicking = false
                                viewModel.sendMsg(SocketMessage.Click(ClickMethod.LEFT_UP))
                            }
                            if (isAClick(start, it.rawX to it.rawY)) {
                                lastClickTime = System.currentTimeMillis()
                                viewModel.sendMsg(
                                    SocketMessage.Click(
                                        ClickMethod.LEFT
                                    )
                                )
                            }

                            start = 0f to 0f
                            lastMove = 0f to 0f
                        }
                        else -> return@pointerInteropFilter false
                    }
                    true
                }) {

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.sendMsg(SocketMessage.Click(ClickMethod.LEFT)) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                    modifier = Modifier
                        .weight(1f)
//                        .pointerInteropFilter {
//                            when (it.action) {
//                                MotionEvent.ACTION_DOWN -> {
//                                    viewModel.sendMsg(Motion.Click(ClickMethod.LEFT_DOWN))
//                                }
//                                MotionEvent.ACTION_UP -> {
//                                    viewModel.sendMsg(Motion.Click(ClickMethod.LEFT_UP))
//                                }
//                                else -> return@pointerInteropFilter false
//                            }
//                            true
//                        }
                ) {}

//                Spacer(modifier = Modifier.fillMaxWidth(0.2f))
                Button(
                    onClick = { viewModel.sendMsg(SocketMessage.Click(ClickMethod.RIGHT)) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                    modifier = Modifier.weight(1f)
                ) {}
            }

        }
    }
}

@Composable
fun HostsDialog(viewModel: HomeViewModel = viewModel()) {
    Dialog(onDismissRequest = { viewModel.hostsDialogVisibility = false }) {
        Card(elevation = 0.dp, shape = RoundedCornerShape(10.dp), backgroundColor = Color.White) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                for (host in viewModel.availableHosts) {
                    Row(modifier = Modifier
                        .clickable {
                            viewModel.inetServerAddress = host
                            viewModel.hostsDialogVisibility = false
                        }
                        .padding(5.dp)) {
                        Text(text = host, modifier = Modifier.weight(1f))
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = "Select"
                        )
                    }

                }
            }
        }
    }
}

private fun isAClick(start: Pair<Float, Float>, end: Pair<Float, Float>): Boolean {
    val differenceX = Math.abs(start.first - end.first)
    val differenceY = Math.abs(start.second - end.second)
    return !(differenceX > 5 /* =5 */ || differenceY > 5)
}