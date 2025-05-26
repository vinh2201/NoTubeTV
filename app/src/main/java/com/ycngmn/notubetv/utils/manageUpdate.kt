package com.ycngmn.notubetv.utils

import android.content.Context

suspend fun getUpdate(context: Context): ReleaseData? {
    try {
        val remoteRelease = fetchUpdate()
        val remoteVersion = remoteRelease.tagName.substring(1) // the 'v'
        val localVersion = getVersion(context)

        if (remoteVersion > localVersion)
            return remoteRelease
        return null
    } catch (_: Exception) { return null }
}