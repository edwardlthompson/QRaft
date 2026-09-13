package org.qraft.app.ui.editor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector
import org.qraft.app.editor.PayloadKind

object PayloadKindIcons {
    fun of(kind: PayloadKind): ImageVector = when (kind) {
        PayloadKind.Url -> Icons.Filled.Link
        PayloadKind.Text -> Icons.Filled.Sms
        PayloadKind.Wifi -> Icons.Filled.Wifi
        PayloadKind.VCard -> Icons.Filled.Person
        PayloadKind.Email -> Icons.Filled.Email
        PayloadKind.Sms -> Icons.Filled.Sms
        PayloadKind.Phone -> Icons.Filled.Phone
        PayloadKind.Crypto -> Icons.Filled.CurrencyBitcoin
        PayloadKind.Calendar -> Icons.Filled.Event
        PayloadKind.Geo -> Icons.Filled.Place
        PayloadKind.WhatsApp -> Icons.AutoMirrored.Filled.Chat
        PayloadKind.AppStore -> Icons.Filled.Store
        PayloadKind.Social -> Icons.Filled.Share
        PayloadKind.MeCard -> Icons.Filled.Badge
        PayloadKind.FaceTime -> Icons.Filled.Videocam
        PayloadKind.Barcode -> Icons.Filled.ViewWeek
    }
}
