package pl.gg.client.ui.base

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.gg.client.R


@Preview
@Composable
fun ButtonsPreview() = Column {
    OutlineButton(
        text = "test",
        textColor = Color.Black,
        icon = R.drawable.ic_wifi
    ) { }

    CardIconButton(
        icon = R.drawable.ic_wifi
    ) {}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OutlineButton(
    text: String,
    textColor: Color,
    icon: Int? = null,
    iconTint: Color? = null,
    onClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Card(
            onClick = onClick,
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            shape = RoundedCornerShape(5.dp)
        ) {
            Row(modifier = Modifier.padding(10.dp)) {

                icon?.let {
                    Image(
                        painterResource(id = icon),
                        null,
                        modifier = Modifier
                            .size(24.dp),
                        colorFilter = ColorFilter.tint(iconTint ?: Color.White)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                }

                Text(
                    text = text,
                    color = textColor,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun CardIconButton(
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.LightGray,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        backgroundColor = backgroundColor
    ) {
        Image(
            painterResource(id = icon),
            null,
            modifier = Modifier
                .padding(10.dp)
                .size(24.dp),
        )
    }
}