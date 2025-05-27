package com.ycngmn.notubetv.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.webkit.PermissionRequest
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import com.multiplatform.webview.web.AccompanistWebChromeClient
import com.multiplatform.webview.web.AccompanistWebViewClient
import com.multiplatform.webview.web.PlatformWebViewParams

@Composable
fun permissionHandlerChrome(context: Context): PlatformWebViewParams {

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    val client = remember {
        object : AccompanistWebViewClient() {
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                Log.e("WebViewError", "Error loading URL: ${request?.url}")
                Log.e("WebViewError", "Error code: ${error?.errorCode}, description: ${error?.description}")

            }

            override fun onReceivedHttpError(
                view: WebView,
                request: WebResourceRequest,
                errorResponse: WebResourceResponse
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                Log.e("WebViewHttpError", "HTTP error on URL: ${request.url}")
                Log.e("WebViewHttpError", "Status: ${errorResponse.statusCode}, Reason: ${errorResponse.reasonPhrase}")
            }
        }
    }

    val chrome = remember {
        object : AccompanistWebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (PermissionRequest.RESOURCE_AUDIO_CAPTURE in request.resources && !hasPermission(context))
                    permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                request.grant(request.resources)
            }
        }
    }
    return PlatformWebViewParams(
        client = client,chromeClient = chrome)
}

fun hasPermission(context: Context) : Boolean  {
    return ContextCompat.checkSelfPermission(
        context, android.Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
}