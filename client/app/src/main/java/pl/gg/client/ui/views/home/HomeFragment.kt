package pl.gg.client.ui.views.home

import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.BackdropValue
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import pl.gg.client.Config
import pl.gg.client.R
import pl.gg.client.ui.BoldText
import pl.gg.client.ui.Title
import pl.gg.client.ui.base.OutlineButton
import pl.gg.client.ui.components.FullscreenProgressIndicator
import pl.gg.client.ui.functional.ClickMethod
import pl.gg.client.ui.functional.KeyboardKey
import pl.gg.client.ui.functional.SocketMessage
import pl.gg.client.ui.functional.clearFocusOnKeyboardDismiss
import pl.gg.client.ui.theme.ClieantSideTheme
import pl.gg.client.ui.theme.Hyperlink

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

        val colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.White,
            activeTickColor = Color.White
        )

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
            .padding(5.dp)
            .height(IntrinsicSize.Min)
    ) {

//        Image(
//            painterResource(id = R.drawable.ic_logo), null, modifier = Modifier.fillMaxSize().background(Color.Red)
//        )

        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxHeight()
                .weight(1f), verticalArrangement = Arrangement.Center
        ) {
            BoldText(
                text = if (!viewModel.inetServerAddress.isNullOrEmpty()) viewModel.inetServerAddress ?: "" else "No host"
            )
            viewModel.state.CreateText()
        }

        Column(
            Modifier
                .clickable {
                    scope.launch {
                        if (state.isRevealed) {
                            state.conceal()
                        } else {
                            state.reveal()
                        }
                    }
                }
                .padding(10.dp)) {
            Image(painterResource(id = R.drawable.ic_settings),
                null,
                alignment = Alignment.TopEnd,
                modifier = Modifier
                    .height(25.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
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
            .alpha(viewModel.state.alpha)
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