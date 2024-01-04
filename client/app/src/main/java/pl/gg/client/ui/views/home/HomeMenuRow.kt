package pl.gg.client.ui.views.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.gg.client.R
import pl.gg.client.ui.Title
import pl.gg.client.ui.base.CardIconButton

enum class HomeMenuItem(
    @DrawableRes val icon: Int,
    val view: @Composable () -> Unit,
    val onClick: HomeViewModel.(HomeMenuItem) -> Unit = { selectMenuItem(it) }
) {
    MOUSE_SPEED(
        R.drawable.ic_speed,
        { MouseSpeedSlider() }
    ),
    SEARCH_FOR_HOSTS(
        R.drawable.ic_wifi,
        { },
        { searchForHosts() }
    ),
    VOLUME(
        R.drawable.ic_volume,
        { VolumeChange() }
    )
}

@Composable
@Preview
fun HomeMenuRowPreview() {
    HomeMenuRow()
}

@Composable
fun HomeMenuRow(
    viewModel: HomeViewModel = viewModel(),
    state: HomeViewModel.State = viewModel.state.collectAsState().value,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val modifier = Modifier.padding(2.dp)
        HomeMenuItem.entries.forEach {
            CardIconButton(
                icon = it.icon,
                modifier = modifier,
                onClick = { it.onClick(viewModel, it) },
                selected = state.selectedMenuItem == it
            )
        }
    }

    Spacer(modifier = Modifier.height(5.dp))

    HomeMenuItem.entries.find { it == state.selectedMenuItem }?.view?.invoke()
}

@Composable
@Preview
fun MouseSliderPreview() {
    MouseSpeedSlider()
}

@Composable
fun MouseSpeedSlider(
    viewModel: HomeViewModel = viewModel(),
    state: HomeViewModel.State = viewModel.state.collectAsState().value,
) = Column {
    val colors = SliderDefaults.colors(
        thumbColor = Color.White,
        activeTrackColor = Color.White,
        activeTickColor = Color.White
    )

    Title(
        name = "Mouse speed: x${state.speed.toInt()}",
        fontSize = 14.sp,
        modifier = Modifier.padding(10.dp)
    )
    Slider(value = state.speed,
        valueRange = 1f..5f,
        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
        steps = 3,
        onValueChange = viewModel::updateSpeed,
        onValueChangeFinished = viewModel::updateConfigMoveSpeed,
        colors = colors
    )
}

@Composable
@Preview
fun VolumeChangePreview() {
    VolumeChange()
}

@Composable
fun VolumeChange(
    viewModel: HomeViewModel = viewModel()
) = Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Title(
        name = "Volume",
        fontSize = 14.sp,
        modifier = Modifier.padding(10.dp)
    )

    val modifier = Modifier.padding(2.dp)

    Row {
        CardIconButton(
            icon = R.drawable.ic_up,
            modifier = modifier,
            onClick = { viewModel.increaseVolume() }
        )

        CardIconButton(
            icon = R.drawable.ic_down,
            modifier = modifier,
            onClick = { viewModel.decreaseVolume() }
        )
    }
}