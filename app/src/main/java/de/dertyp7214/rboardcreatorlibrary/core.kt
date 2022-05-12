package de.dertyp7214.rboardcreatorlibrary

import java.util.*

fun String.cap(): String = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(
        Locale.getDefault()
    ) else it.toString()
}