package pl.gg.client.ui.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

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