package com.ycngmn.notubetv.ui.screens

import android.view.View
import android.webkit.CookieManager
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState

@Composable
fun YoutubeWV() {

    val state = rememberWebViewState("https://www.youtube.com/tv?hrld=1&fltor=1#/watch?v=xc-WLvj0zHk")
    val navigator = rememberWebViewNavigator()

    LaunchedEffect(state.loadingState) {
        if (state.loadingState is LoadingState.Finished) {
            navigator.evaluateJavaScript(
                """
                    (function() {
                        var existing = document.querySelector('meta[name="viewport"]');
                        if (existing) {
                            existing.setAttribute('content', 'width=3840, height=2160, initial-scale=1.0');
                        } else {
                            var meta = document.createElement('meta');
                            meta.name = 'viewport';
                            meta.content = 'width=3840, height=2160, initial-scale=1.0';
                            document.head.appendChild(meta);
                        }
                    })();
                """.trimIndent()
            )
        }
    }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }


    WebView(
        modifier = Modifier
            .size(
                with(density) { screenWidthPx.toDp() },
                with(density) { screenHeightPx.toDp() }
            ),
        state = state,
        navigator = navigator,
        onCreated = { webView ->
            // Set up cookies
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(webView, true)
            cookieManager.flush()

            state.webSettings.apply {
                customUserAgentString = "Roku/DVP-5.0 (025.00E08043A)"
                isJavaScriptEnabled = true


                androidWebSettings.apply {
                    isDebugInspectorInfoEnabled = true
                    useWideViewPort = true
                    domStorageEnabled = true
                    hideDefaultVideoPoster = true
                    mediaPlaybackRequiresUserGesture = false
                    allowFileAccess = true
                }
            }

            webView.apply {
                setLayerType(View.LAYER_TYPE_HARDWARE, null)
                setInitialScale(35)

                // Hide scrollbars
                overScrollMode = View.OVER_SCROLL_NEVER
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false

                settings.setSupportZoom(true)
                settings.loadWithOverviewMode = true
            }
        }
    )

}