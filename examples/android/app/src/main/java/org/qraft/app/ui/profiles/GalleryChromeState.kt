package org.qraft.app.ui.profiles

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.qraft.data.ProfileSearch

enum class GalleryListKind { Empty, Miss, Grid }

fun galleryListKind(total: Int, shown: Int): GalleryListKind = when {
    total <= 0 -> GalleryListKind.Empty
    shown <= 0 -> GalleryListKind.Miss
    else -> GalleryListKind.Grid
}

/** Gallery search / sort / filter panel state shared with the TopAppBar. */
class GalleryChromeState(
    searchOpen: Boolean = false,
    filterOpen: Boolean = false,
    query: String = "",
    sort: ProfileSearch.Sort = ProfileSearch.Sort.NEWEST,
) {
    var searchOpen by mutableStateOf(searchOpen)
    var filterOpen by mutableStateOf(filterOpen)
    var query by mutableStateOf(query)
    var sort by mutableStateOf(sort)

    fun toggleSearch() {
        searchOpen = !searchOpen
    }

    fun toggleFilter() {
        filterOpen = !filterOpen
    }

    fun openBackup() {
        filterOpen = true
    }

    fun collapse() {
        searchOpen = false
        filterOpen = false
    }
}
