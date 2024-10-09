import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.singleWindowApplication
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.KeyboardFocusManager
import java.io.File
import kotlin.math.max

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
    // region GlobalKeyEvent
    // GlobalScreen.registerNativeHook()
    // GlobalScreen.addNativeKeyListener(GlobalKeyListener())

    if (cleared) {
        Text("Cleared")
    }
    // endregion

    // region Window (SingleWindowApplication에서는 사용 불가)
//    Window(
//        onCloseRequest = { isVisible = false },
//        visible = isVisible,
//        title = "DesktopAppPractice",
//    ) {
    // endregion

    // region Dialog
//    Button(onClick = { isDialogOpen = true }) {
//        Text("Open Dialog")
//    }
    var isDialogOpen by remember { mutableStateOf(false) }
    if (isDialogOpen) {
        // TODO: 생각했던 dialog가 아닌데..
        DialogWindow(
            onCloseRequest = { isDialogOpen = false },
            state = rememberDialogState()
        ) {
            Text("Dialog Content")
        }
    }
    // endregion

    // region Tray (SingleWindowApplication에서는 사용 불가)
    var isVisible by remember { mutableStateOf(true) }
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
    // endregion

    var commandResult by remember { mutableStateOf("") }

    // region webview init
    var restartRequired by remember { mutableStateOf(false) }
    var downloading by remember { mutableStateOf(0F) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            KCEF.init(builder = {
                installDir(File("kcef-bundle"))
                progress {
                    onDownloading {
                        downloading = max(it, 0F)
                    }
                    onInitialized {
                        initialized = true
                    }
                }
                settings {
                    cachePath = File("cache").absolutePath
                }
            }, onError = {
                it?.printStackTrace()
            }, onRestartRequired = {
                restartRequired = true
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            KCEF.disposeBlocking()
        }
    }
    // endregion

    if (restartRequired) {
        Text(text = "Restart required.")
    } else {
        if (initialized) {
            Column(modifier = Modifier.fillMaxSize()) {
                App(
                    commandResult = commandResult,
                    onCommandRunClick = {
                        commandResult = commandExecutor.execute("ls")
                    }
                )
                WebViewSample()
            }
        } else {
            Text(text = "Downloading $downloading%")
        }
    }
//    }
}

@Composable
internal fun WebViewSample() {
    MaterialTheme {
        val webViewState =
            rememberWebViewState("https://github.com/KevinnZou/compose-webview-multiplatform")
        webViewState.webSettings.apply {
            isJavaScriptEnabled = true
            customUserAgentString =
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1) AppleWebKit/625.20 (KHTML, like Gecko) Version/14.3.43 Safari/625.20"
            androidWebSettings.apply {
                isAlgorithmicDarkeningAllowed = true
                safeBrowsingEnabled = true
            }
        }
        Column(Modifier.fillMaxSize()) {
            val text =
                webViewState.let {
                    "${it.pageTitle ?: ""} ${it.loadingState} ${it.lastLoadedUrl ?: ""}"
                }
            Text(text)
            WebView(
                state = webViewState,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}