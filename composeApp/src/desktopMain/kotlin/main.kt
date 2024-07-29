import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.singleWindowApplication
import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import java.awt.KeyboardFocusManager

val commandExecutor = CommandExecutor()

private var cleared by mutableStateOf(false)

// Window에 포커스가 있을때만 동작함
private val manager = KeyboardFocusManager.getCurrentKeyboardFocusManager().apply {
    addKeyEventDispatcher {
//        println(it)
        if (
            it.isControlDown &&
            it.isShiftDown &&
            it.keyChar == 'c'
//            it.keyCode == KeyEvent.KEY_PRESSED
        ) {
//            cleared = true
            true
        } else {
            false
        }
    }
}

// 글로벌 키보드 이벤트 리스너
class GlobalKeyListener : NativeKeyListener {
    private val keyCodeSet = mutableSetOf<Int>()

    override fun nativeKeyPressed(nativeEvent: NativeKeyEvent) {
        println("Key Pressed: ${NativeKeyEvent.getKeyText(nativeEvent.keyCode)}")
        keyCodeSet.add(nativeEvent.keyCode)
    }

    override fun nativeKeyReleased(nativeEvent: NativeKeyEvent) {
        println("Key Released: ${NativeKeyEvent.getKeyText(nativeEvent.keyCode)}")
        if (nativeEvent.keyCode == NativeKeyEvent.VC_C) {
            if (keyCodeSet.contains(NativeKeyEvent.VC_CONTROL) && keyCodeSet.contains(NativeKeyEvent.VC_META)) {
                cleared = true
            }
        }
        keyCodeSet.remove(nativeEvent.keyCode)
    }
}

fun main() = singleWindowApplication(
    // Window 포커스가 있을 때 동작
    onKeyEvent = {
        if (
            it.isCtrlPressed &&
            it.isShiftPressed &&
            it.key == Key.C &&
            it.type == KeyEventType.KeyDown
        ) {
//            cleared = true
            true
        } else {
            false
        }
    }
) {
    GlobalScreen.registerNativeHook()
    GlobalScreen.addNativeKeyListener(GlobalKeyListener())

    if (cleared) {
        Text("Cleared")
    }

    var isVisible by remember { mutableStateOf(true) }
//    Window(
//        onCloseRequest = { isVisible = false },
//        visible = isVisible,
//        title = "DesktopAppPractice",
//    ) {
    var isDialogOpen by remember { mutableStateOf(false) }

    var commandResult by remember { mutableStateOf("") }

//    Button(onClick = { isDialogOpen = true }) {
//        Text("Open Dialog")
//    }

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