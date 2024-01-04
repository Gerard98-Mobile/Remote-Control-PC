package pl.gg.client.ui.data

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import pl.gg.client.ui.theme.DarkGreen
import pl.gg.client.ui.theme.DarkRed

enum class ConnectionState(private val tint: Color, val alpha: Float, private val text: String) {
    CONNECTED(DarkGreen, 1f, "Connected"),
    DISCONNECTED(DarkRed, 0.5f, "Disconnected");

    @Composable
    fun CreateText() = Text(text, color = tint)
}
