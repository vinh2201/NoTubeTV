package com.ycngmn.notubetv.utils

import android.webkit.JavascriptInterface
import androidx.compose.runtime.MutableState

class PermissionBridge(val permissionTrigger: MutableState<Boolean>) {
    @JavascriptInterface
    fun onTrigger() { permissionTrigger.value = true }
}