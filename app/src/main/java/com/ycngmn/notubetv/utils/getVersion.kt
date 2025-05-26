package com.ycngmn.notubetv.utils

import android.content.Context

fun getVersion(context: Context): String {
    val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    return pInfo.versionName.toString()
}


