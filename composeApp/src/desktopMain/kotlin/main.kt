import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberDialogState

val commandExecutor = CommandExecutor()

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DesktopAppPractice",
    ) {
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

        App(
            commandResult = commandResult,
            onCommandRunClick = {
                commandResult = commandExecutor.execute("ls")
            }
        )
    }
}