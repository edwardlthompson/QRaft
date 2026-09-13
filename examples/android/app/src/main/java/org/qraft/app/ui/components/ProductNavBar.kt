package org.qraft.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.ui.nav.GpRoute

@Composable
fun ProductNavBar(
    current: GpRoute,
    onSelect: (GpRoute) -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = current == GpRoute.Home,
            onClick = { onSelect(GpRoute.Home) },
            icon = { Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.editor_title)) },
            label = { Text(stringResource(R.string.editor_title)) },
        )
        NavigationBarItem(
            selected = current == GpRoute.Profiles,
            onClick = { onSelect(GpRoute.Profiles) },
            icon = {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = stringResource(R.string.nav_gallery))
            },
            label = { Text(stringResource(R.string.nav_gallery)) },
        )
        NavigationBarItem(
            selected = current == GpRoute.Scan,
            onClick = { onSelect(GpRoute.Scan) },
            icon = {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = stringResource(R.string.nav_scan))
            },
            label = { Text(stringResource(R.string.nav_scan)) },
        )
        NavigationBarItem(
            selected = current == GpRoute.Wallpaper,
            onClick = { onSelect(GpRoute.Wallpaper) },
            icon = { Icon(Icons.Filled.Wallpaper, contentDescription = stringResource(R.string.nav_wallpaper)) },
            label = { Text(stringResource(R.string.nav_wallpaper)) },
        )
    }
}
