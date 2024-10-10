import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData

@Composable
internal fun EditorWebView() {
//    val webViewState = rememberWebViewStateWithHTMLFile(
//        fileName = "index.html",
//    )
    val webViewState = rememberWebViewStateWithHTMLData(
        data = """
            <html>
                <head>
                    <title>Test</title>
                </head>
                <body>
                    <h1>Hello, World!</h1>
                </body>
            </html>
        """.trimIndent()
    )
    val webViewNavigator = rememberWebViewNavigator()
    LaunchedEffect(Unit) {
        webViewState.webSettings.apply {
            isJavaScriptEnabled = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
//            customUserAgentString =
//                "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1) AppleWebKit/625.20 (KHTML, like Gecko) Version/14.3.43 Safari/625.20"
//            androidWebSettings.apply {
//                isAlgorithmicDarkeningAllowed = true
//                safeBrowsingEnabled = true
//            }
        }
    }

    MaterialTheme {
//        val webViewState =
//            rememberWebViewState("https://github.com/KevinnZou/compose-webview-multiplatform")
        Column(Modifier.fillMaxSize()) {
            val text =
                webViewState.let {
                    "${it.pageTitle ?: ""} ${it.loadingState} ${it.lastLoadedUrl ?: ""}"
                }
            Text(text)
            WebView(
                state = webViewState,
                modifier = Modifier.fillMaxSize(),
                navigator = webViewNavigator,
            )
        }
    }
}