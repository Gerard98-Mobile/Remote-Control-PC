package pl.gg.client.ui.views.home

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
class HomeState(
    private val coroutineScope: CoroutineScope,
    val scaffold: BackdropScaffoldState,
) {

    fun showSnackbar(message: String) = coroutineScope.launch {
        scaffold.snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short
        )
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberHomeState(
    initState: BackdropValue,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    scaffoldState: BackdropScaffoldState = rememberBackdropScaffoldState(initState),
) = remember(coroutineScope, scaffoldState) {
    HomeState(coroutineScope, scaffoldState)
}