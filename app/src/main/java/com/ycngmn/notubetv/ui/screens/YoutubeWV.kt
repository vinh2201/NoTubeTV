package com.ycngmn.notubetv.ui.screens

import android.annotation.SuppressLint
import android.view.View
import android.webkit.CookieManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.ycngmn.notubetv.R
import com.ycngmn.notubetv.utils.ExitBridge
import com.ycngmn.notubetv.utils.fetchScripts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun YoutubeWV() {

    val context = LocalContext.current
    val scripts = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            scripts.value = fetchScripts()
        }
    }

    val state = rememberWebViewState("https://www.youtube.com/tv")
    val navigator = rememberWebViewNavigator()

    val exitTrigger = remember { mutableStateOf(false) }

    BackHandler {
        navigator.evaluateJavaScript(
            context.resources.openRawResource(R.raw.back_bridge)
                .bufferedReader().use { it.readText() }
        )
    }

    LaunchedEffect(scripts.value, state.loadingState) {
        if (scripts.value.isNotEmpty())
            navigator.evaluateJavaScript(scripts.value)
    }

    if (exitTrigger.value) exitProcess(0)

    val config = LocalConfiguration.current

    WebView(
        captureBackPresses = false,
        modifier = Modifier
            .size(
                config.screenWidthDp.dp ,
                config.screenHeightDp.dp
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
                customUserAgentString = "Mozilla/5.0 (Linux; Android 12) Cobalt/22.2.3-gold (PS4)"
                isJavaScriptEnabled = true

                androidWebSettings.apply {
                    //isDebugInspectorInfoEnabled = true
                    useWideViewPort = true
                    domStorageEnabled = true
                    hideDefaultVideoPoster = true
                    mediaPlaybackRequiresUserGesture = false
                    allowFileAccess = true
                }
            }

            webView.apply {

                addJavascriptInterface(ExitBridge(exitTrigger), "ExitBridge")

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