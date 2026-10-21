package org.qraft.app.editor

import androidx.annotation.StringRes
import org.qraft.app.R

/** One-line “what this QR will do” under the kind chips. */
object KindHints {
    @StringRes
    fun of(kind: PayloadKind): Int = when (kind) {
        PayloadKind.Url -> R.string.kind_hint_url
        PayloadKind.Text -> R.string.kind_hint_text
        PayloadKind.Wifi -> R.string.kind_hint_wifi
        PayloadKind.VCard -> R.string.kind_hint_vcard
        PayloadKind.Email -> R.string.kind_hint_email
        PayloadKind.Sms -> R.string.kind_hint_sms
        PayloadKind.Phone -> R.string.kind_hint_phone
        PayloadKind.Crypto -> R.string.kind_hint_crypto
        PayloadKind.Calendar -> R.string.kind_hint_calendar
        PayloadKind.Geo -> R.string.kind_hint_geo
        PayloadKind.WhatsApp -> R.string.kind_hint_whatsapp
        PayloadKind.AppStore -> R.string.kind_hint_appstore
        PayloadKind.Social -> R.string.kind_hint_social
        PayloadKind.MeCard -> R.string.kind_hint_mecard
        PayloadKind.FaceTime -> R.string.kind_hint_facetime
        PayloadKind.Barcode -> R.string.kind_hint_barcode
    }
}
