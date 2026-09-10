package org.qraft.data

object ProfileTags {
    fun parse(raw: String): List<String> =
        raw.split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()

    fun format(tags: List<String>): String = tags.joinToString(", ")
}
