package org.qraft.app.ui.editor

import org.qraft.app.R
import org.qraft.app.editor.PayloadKind

internal fun kindLabelRes(kind: PayloadKind): Int = when (kind) {
    PayloadKind.Url -> R.string.editor_kind_url
    PayloadKind.Text -> R.string.editor_kind_text
    PayloadKind.Wifi -> R.string.editor_kind_wifi
    PayloadKind.VCard -> R.string.editor_kind_vcard
    PayloadKind.Email -> R.string.editor_kind_email
    PayloadKind.Sms -> R.string.editor_kind_sms
    PayloadKind.Phone -> R.string.editor_kind_phone
    PayloadKind.Crypto -> R.string.editor_kind_crypto
    PayloadKind.Calendar -> R.string.editor_kind_calendar
    PayloadKind.Geo -> R.string.editor_kind_geo
    PayloadKind.WhatsApp -> R.string.editor_kind_whatsapp
    PayloadKind.AppStore -> R.string.editor_kind_appstore
    PayloadKind.Social -> R.string.editor_kind_social
    PayloadKind.MeCard -> R.string.editor_kind_mecard
    PayloadKind.FaceTime -> R.string.editor_kind_facetime
    PayloadKind.Barcode -> R.string.editor_kind_barcode
}
