package com.ycngmn.notubetv.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.ycngmn.notubetv.utils.ExitBridge
import com.ycngmn.notubetv.utils.PermissionBridge
import com.ycngmn.notubetv.utils.fetchScripts
import com.ycngmn.notubetv.utils.hasPermission
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
    val permissionTrigger = rememberSaveable { mutableStateOf(false) }

    BackHandler {
        navigator.evaluateJavaScript(
            context.resources.openRawResource(R.raw.back_bridge)
                .bufferedReader().use { it.readText() }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) permissionTrigger.value = false
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            scripts.value = fetchScripts()
        }
    }

    LaunchedEffect(scripts.value, state.loadingState) {
        if (scripts.value.isNotEmpty() && state.loadingState is LoadingState.Finished)
            navigator.evaluateJavaScript(scripts.value)
    }

    if (exitTrigger.value) activity.finish()

    val config = LocalConfiguration.current

    if (!hasPermission(context) && permissionTrigger.value) {
        permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO) }

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
                customUserAgentString = "Mozilla/5.0 (PS4; Leanback Shell) Cobalt/24.lts.13.1032728-gold (Sony, PS4, Wired)"
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
                addJavascriptInterface(PermissionBridge(permissionTrigger), "PermissionBridge")

                setLayerType(View.LAYER_TYPE_HARDWARE, null)
                setInitialScale(35)

                // Hide scrollbars
                overScrollMode = View.OVER_SCROLL_NEVER
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false

                settings.setSupportZoom(true)
                settings.loadWithOverviewMode = true

                webChromeClient = object : WebChromeClient() {
                    override fun onPermissionRequest(request: PermissionRequest?) {
                        request?.grant(request.resources)
                    }
                }
            }
        }
    )
}