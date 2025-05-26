package com.ycngmn.notubetv.utils

import android.content.Context
import com.multiplatform.webview.web.WebViewNavigator

suspend fun getUpdate(context: Context, navigator: WebViewNavigator, callback: (ReleaseData?) -> Unit) {
    try {
        val remoteRelease = fetchUpdate()
        val remoteVersion = remoteRelease.tagName.removePrefix("v")
        val localVersion = getVersion(context)

        if (remoteVersion > localVersion) {
            getSkipVersion(navigator) {
                val skipVersion = it?.removeSurrounding("\"")?.removePrefix("v")
                if (skipVersion != remoteVersion)
                    callback(remoteRelease)
                else callback(null)
            }
        }
        else callback(null)

    } catch (_: Exception) { callback(null) }
}

fun getSkipVersion(navigator: WebViewNavigator, callback: (String?) -> Unit) {
    navigator.evaluateJavaScript("configRead('skipVersionName')") {
        callback(it)
    }
}