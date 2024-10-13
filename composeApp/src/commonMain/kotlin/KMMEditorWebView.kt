import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.util.KLogSeverity
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
import desktopapppractice.composeapp.generated.resources.Res
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun KMMEditorWebView() {
    println("KMMEditorWebView")

    var count by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    val webViewState = rememberWebViewStateWithHTMLData("", "${baseUrl}/index")
//    val webViewState = rememberWebViewState("https://naver.com")
    val webViewNavigator = rememberWebViewNavigator()
    val jsBridge = rememberWebViewJsBridge(webViewNavigator)

    LaunchedEffect(Unit) {
        initWebView(webViewState)
//        initJsBridge(jsBridge)
    }

    LaunchedEffect(Unit) {
        val res = Res.readBytes(path).decodeToString().trimIndent()
        delay(500)
        println("load editor")
        webViewNavigator.loadHtml(res, "${baseUrl}/editor")
    }

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (webViewState.isLoading) {
                LinearProgressIndicator(
                    progress = 0f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                "count: $count",
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Column(Modifier.fillMaxSize()) {
                ButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                    copyClick = {
                        webViewNavigator.evaluateJavaScript("return getCode();") {
                            println("codeString: $it")
                        }
                    },
                    loadClick = {
                        scope.launch {
                            count += 1
//                            val res = Res.readBytes(path).decodeToString().trimIndent()
//                            webViewNavigator.loadHtml(res, "${baseUrl}/editor")
                        }
                    }
                )
                WebView(
                    state = webViewState,
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    captureBackPresses = false,
                    navigator = webViewNavigator,
//                    webViewJsBridge = jsBridge,
                )
            }
        }
    }
}

@Composable
fun ButtonRow(
    modifier: Modifier = Modifier,
    copyClick: () -> Unit = {},
    loadClick: () -> Unit = {},
) {
    Row(modifier = modifier) {
        Button(onClick = copyClick) {
            Text("복사하기")
        }
        Button(onClick = loadClick) {
            Text("로드")
        }
    }
}

fun initWebView(webViewState: WebViewState) {
    webViewState.webSettings.apply {
        zoomLevel = 1.0
        isJavaScriptEnabled = true
        logSeverity = KLogSeverity.Debug
        allowFileAccessFromFileURLs = true
        allowUniversalAccessFromFileURLs = true
        androidWebSettings.apply {
            isAlgorithmicDarkeningAllowed = true
            safeBrowsingEnabled = true
            allowFileAccess = true
        }
    }
}

suspend fun initJsBridge(webViewJsBridge: WebViewJsBridge) {
    webViewJsBridge.register(DefaultJsMessageHandler())
}

class DefaultJsMessageHandler : IJsMessageHandler {
    override fun methodName() = "Default"

    override fun handle(
        message: JsMessage,
        navigator: WebViewNavigator?,
        callback: (String) -> Unit,
    ) {
        // TODO
    }
}
