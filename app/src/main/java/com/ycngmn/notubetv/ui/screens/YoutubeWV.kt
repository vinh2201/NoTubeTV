package com.ycngmn.notubetv.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.ycngmn.notubetv.R
import com.ycngmn.notubetv.ui.components.UpdateDialog
import com.ycngmn.notubetv.utils.ExitBridge
import com.ycngmn.notubetv.utils.ReleaseData
import com.ycngmn.notubetv.utils.fetchScripts
import com.ycngmn.notubetv.utils.getUpdate
import com.ycngmn.notubetv.utils.permissionHandlerChrome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun YoutubeWV() {

    val context = LocalContext.current
    val activity = context as Activity

    val state = rememberWebViewState("https://www.youtube.com/tv")
    val navigator = rememberWebViewNavigator()

    val scripts = rememberSaveable { mutableStateOf("") }
    val exitTrigger = remember { mutableStateOf(false) }

    val updateData = remember { mutableStateOf<ReleaseData?>(null) }


    BackHandler {
        navigator.evaluateJavaScript(
            context.resources.openRawResource(R.raw.back_bridge)
                .bufferedReader().use { it.readText() }
        )
    }


    LaunchedEffect(Unit) {
        val fetchedScripts = withContext(Dispatchers.IO) { fetchScripts() }
        val update = withContext(Dispatchers.IO) { getUpdate(context) }

        scripts.value = fetchedScripts
        if (update!= null) updateData.value = update
    }

    if (updateData.value != null) UpdateDialog(updateData.value!!)

    LaunchedEffect(scripts.value, state.loadingState) {
        if (scripts.value.isNotEmpty() && state.loadingState is LoadingState.Finished)
            navigator.evaluateJavaScript(scripts.value)
    }

    if (exitTrigger.value) activity.finish()

    val config = LocalConfiguration.current

    WebView(
        modifier = Modifier.size(
            config.screenWidthDp.dp,
            config.screenHeightDp.dp
        ),
        state = state,
        navigator = navigator,
        platformWebViewParams = permissionHandlerChrome(context),
        captureBackPresses = false,
        onCreated = { webView ->
            // Set up cookies
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(webView, true)
            cookieManager.flush()

            state.webSettings.apply {
                customUserAgentString = "Mozilla/5.0 (PS4; Leanback Shell) Cobalt/24.lts.13.1032728-gold (Sony, PS4, Wired)"
                isJavaScriptEnabled = true

                androidWebSettings.apply {
                    //isDebugInspectorInfoEnabled = true
                    useWideViewPort = true
                    domStorageEnabled = true
                    hideDefaultVideoPoster = true
                    mediaPlaybackRequiresUserGesture = false
                }
            }

            webView.apply {
                addJavascriptInterface(ExitBridge(exitTrigger), "ExitBridge")

                setLayerType(View.LAYER_TYPE_HARDWARE, null)
                setInitialScale(35)

                // Hide scrollbars
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false

                settings.setSupportZoom(true)
                settings.loadWithOverviewMode = true
                settings.mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW
            }
        }
    )
}