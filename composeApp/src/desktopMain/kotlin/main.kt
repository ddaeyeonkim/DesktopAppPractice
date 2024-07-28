import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

val commandExecutor = CommandExecutor()

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DesktopAppPractice",
    ) {
        var commandResult by remember { mutableStateOf("") }

        App(
            commandResult = commandResult,
            onCommandRunClick = {
                commandResult = commandExecutor.execute("ls")
            }
        )
    }
}