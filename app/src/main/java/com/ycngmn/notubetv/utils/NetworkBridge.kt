package com.ycngmn.notubetv.utils

import android.webkit.JavascriptInterface
import com.multiplatform.webview.web.WebViewNavigator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class NetworkBridge(val navigator: WebViewNavigator) {
    private val client = HttpClient(CIO)

    @JavascriptInterface
    fun fetch(url: String, videoId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val body = client.get(url).body<String>()
            val filteredBody =
                if (body.startsWith("[")) filterSponsorBlock(body, videoId)
                else body
            val js = "window.onNetworkBridgeResponse('$filteredBody');"
            navigator.evaluateJavaScript(js)
        }
    }

    private fun filterSponsorBlock(body: String, videoId: String): String {
        val json = JSONArray(body)

        for (i in 0 until json.length()) {
            val item = json.getJSONObject(i)
            if (item.getString("videoID") == videoId) {
                return item.toString()
            }
        }
        return ""
    }
}