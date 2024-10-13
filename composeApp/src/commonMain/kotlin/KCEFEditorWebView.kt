import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.text.style.TextAlign
import desktopapppractice.composeapp.generated.resources.Res
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.browser.CefRendering
import org.cef.handler.CefLoadHandler
import org.cef.network.CefRequest
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun KCEFEditorWebView() {
    var isLoading by remember { mutableStateOf(false) }

    var count by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    val client = remember { KCEF.newClientBlocking() }
    val browser = remember {
        client.createBrowserWithHtml(
            "<html></html>",
            "${baseUrl}/index",
            CefRendering.DEFAULT,
            false
        )
    }

    LaunchedEffect(Unit) {
        client.addLoadHandler(object : CefLoadHandler {
            override fun onLoadingStateChange(browser: CefBrowser?, loading: Boolean, canGoBack: Boolean, canGoForward: Boolean) {
                println("onLoadingStateChange ${browser?.url}")
                isLoading = loading
            }

            override fun onLoadStart(browser: CefBrowser?, frame: CefFrame?, transitionType: CefRequest.TransitionType?) {
                isLoading = true
                println("onLoadStart ${browser?.url}")
            }

            override fun onLoadEnd(b: CefBrowser, frame: CefFrame?, p2: Int) {
                println("onLoadEnd ${b.url}")
                isLoading = false

                when {
                    b.url.contains("/index") -> {
                        scope.launch(Dispatchers.IO) {
                            val res = Res.readBytes(path).decodeToString().trimIndent()
                            browser.loadHtml(res, "${baseUrl}/editor")
                        }
                    }

                    b.url.contains("/editor") -> {
                        scope.launch {
                            delay(300)
                            val result = browser.evaluateJavaScript(
                                """
                                updateCode("hi");
                                """.trimIndent()
                            )
                            println(result)
                        }
                    }
                }
            }

            override fun onLoadError(browser: CefBrowser?, frame: CefFrame?, errorCode: CefLoadHandler.ErrorCode?, errorText: String?, failedUrl: String?) {
                println("onLoadError ${browser?.url}")
                isLoading = false
            }
        })
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
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
                    scope.launch {
                        val codeString = browser.evaluateJavaScript("return getCode();")
                        println("codeString: $codeString")
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
            SwingPanel(
                factory = {
                    browser.uiComponent
                },
                modifier = Modifier.fillMaxWidth().weight(1f),
            )
        }
    }
}