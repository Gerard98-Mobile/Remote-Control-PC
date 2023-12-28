package pl.gg.client.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle

@Composable
fun ShimmerText(
    text: String,
    style: TextStyle = LocalTextStyle.current,
    colorStops: Array<Pair<Float, Color>> = arrayOf(
        0.0f to Color.Black,
        1f to Color.Magenta
    )
) {
    val mergedStyle = style.merge(LocalTextStyle.current)

    val currentFontSizePx = with(LocalDensity.current) { mergedStyle.fontSize.toPx() }
    val currentFontSizeDoublePx = currentFontSizePx * 2

    val offset by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0f,
        targetValue = currentFontSizeDoublePx,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)),
        label = ""
    )

    val brush = Brush.linearGradient(
        colorStops = colorStops,
        start = Offset(offset, offset),
        end = Offset(offset + currentFontSizePx, offset + currentFontSizePx),
        tileMode = TileMode.Mirror
    )

    Text(
        text = "Trying to reconnect...",
        style = mergedStyle.copy(
            brush = brush,
        )
    )
}