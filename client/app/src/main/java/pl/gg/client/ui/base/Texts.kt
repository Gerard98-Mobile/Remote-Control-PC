package pl.gg.client.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import pl.gg.client.ui.theme.DarkOrange


@Composable
fun Title(name: String, modifier: Modifier = Modifier, fontSize: TextUnit = TextUnit.Unspecified) {
    Text(text = name, modifier = modifier, fontSize = fontSize, fontWeight = FontWeight.Bold)
}

@Composable
fun BoldText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    style: TextStyle = LocalTextStyle.current){

    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
        fontSize = fontSize,
        color = color,
        textAlign = textAlign,
        style = style)
}

