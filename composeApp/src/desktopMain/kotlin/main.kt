import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.singleWindowApplication

val commandExecutor = CommandExecutor()

fun main() = singleWindowApplication {
    var isVisible by remember { mutableStateOf(true) }
//    Window(
//        onCloseRequest = { isVisible = false },
//        visible = isVisible,
//        title = "DesktopAppPractice",
//    ) {
    var isDialogOpen by remember { mutableStateOf(false) }

    var commandResult by remember { mutableStateOf("") }

    Button(onClick = { isDialogOpen = true }) {
        Text("Open Dialog")
    }

    if (isDialogOpen) {
        // TODO: 생각했던 dialog가 아닌데..
        DialogWindow(
            onCloseRequest = { isDialogOpen = false },
            state = rememberDialogState()
        ) {
            Text("Dialog Content")
        }
    }

    if (!isVisible) {
//            Tray(
//                painterResource(Res.drawable.compose_multiplatform),
//                tooltip = "DesktopAppPractice",
//                onAction = { isVisible = true },
//                menu = {
//                    Item("Exit", onClick = ::exitApplication)
//                }
//            )
    }

    App(
        commandResult = commandResult,
        onCommandRunClick = {
            commandResult = commandExecutor.execute("ls")
        }
    )
//    }
}