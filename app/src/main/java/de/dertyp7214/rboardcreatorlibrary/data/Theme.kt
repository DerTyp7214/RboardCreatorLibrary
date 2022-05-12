package de.dertyp7214.rboardcreatorlibrary.data

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.utils.URLEncodedUtils
import java.net.URI
import java.nio.charset.Charset

data class Theme(
    val url: String,
    val preview: String,
    val download: String,
    val name: String,
    val author: String,
    val parameters: Map<String, String> = mapOf()
) {
    companion object {
        fun parseTheme(url: String): Theme {
            val params = URLEncodedUtils.parse(URI(url), Charset.forName("UTF-8"))
                .associate { Pair(it.name, it.value) }
            return Theme(
                url,
                url.replace("dertyp7214.de", "dertyp7214.de/preview"),
                url.replace("dertyp7214.de", "dertyp7214.de/get"),
                params["themeName"] ?: "ThemeName",
                params["author"] ?: "DerTyp7214",
                params
            )
        }

        fun validateTheme(url: String): Boolean {
            val params = URLEncodedUtils.parse(URI(url), Charset.forName("UTF-8"))
                .associate { Pair(it.name, it.value) }
            return url.startsWith("https://creator.dertyp7214.de") && params.containsKey("themeName") && params.containsKey(
                "author"
            )
        }
    }
}