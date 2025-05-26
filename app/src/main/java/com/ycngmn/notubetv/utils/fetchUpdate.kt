package com.ycngmn.notubetv.utils

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import org.json.JSONObject

data class ReleaseData (
    val tagName: String,
    val changelog: String,
    val downloadUrl: String
)

suspend fun fetchUpdate() : ReleaseData {
    val fetchUrl = "https://api.github.com/repos/ycngmn/notubetv/releases/latest"
    val client = HttpClient(CIO)
    val req = client.get(fetchUrl)
    val res = JSONObject(req.body() as String)

    val commitSHA = Regex("\\b[a-fA-F0-9]{40}\\b")

    return ReleaseData(
        tagName = res.getString("tag_name"),
        changelog = res.getString("body")
            .substringAfter("</ins>").replace(commitSHA, "").replace(Regex("\\s{2,}"), " "),
        downloadUrl = res.getJSONArray("assets").getJSONObject(0).getString("browser_download_url")
    )
}