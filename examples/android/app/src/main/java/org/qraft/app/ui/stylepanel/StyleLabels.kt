package org.qraft.app.ui.stylepanel

import org.qraft.app.R
import org.qraft.coreqr.WifiSecurity
import org.qraft.render.CenterMark
import org.qraft.render.ColorTheme
import org.qraft.render.FinderShape
import org.qraft.render.ModuleShape
import org.qraft.render.QrFrame
import org.qraft.render.QrStyle
import org.qraft.render.StylePresets

object StyleLabels {
    fun moduleLabelRes(shape: ModuleShape): Int = when (shape) {
        ModuleShape.SQUARE -> R.string.style_module_square
        ModuleShape.ROUNDED -> R.string.style_module_rounded
        ModuleShape.DOT -> R.string.style_module_dot
        ModuleShape.DIAMOND -> R.string.style_module_diamond
        ModuleShape.PILL_H -> R.string.style_module_pill_h
        ModuleShape.PILL_V -> R.string.style_module_pill_v
        ModuleShape.BLOB -> R.string.style_module_blob
    }

    fun themeLabelRes(theme: ColorTheme): Int = when (theme) {
        ColorTheme.CLASSIC -> R.string.style_theme_classic
        ColorTheme.OLED -> R.string.style_theme_oled
        ColorTheme.INVERT -> R.string.style_theme_invert
        ColorTheme.BLUE -> R.string.style_theme_blue
        ColorTheme.FOREST -> R.string.style_theme_forest
        ColorTheme.SUNSET -> R.string.style_theme_sunset
    }

    fun finderLabelRes(shape: FinderShape): Int = when (shape) {
        FinderShape.SQUARE -> R.string.style_finder_square
        FinderShape.ROUNDED -> R.string.style_finder_rounded
        FinderShape.CIRCLE -> R.string.style_finder_circle
        FinderShape.HEX -> R.string.style_finder_hex
        FinderShape.RING -> R.string.style_finder_ring
    }

    fun frameLabelRes(frame: QrFrame): Int = when (frame) {
        QrFrame.NONE -> R.string.style_frame_none
        QrFrame.THIN -> R.string.style_frame_thin
        QrFrame.BOLD -> R.string.style_frame_bold
        QrFrame.ROUNDED -> R.string.style_frame_rounded
    }

    fun centerLabelRes(mark: CenterMark): Int = when (mark) {
        CenterMark.NONE -> R.string.style_center_none
        CenterMark.CUTOUT -> R.string.style_center_cutout
        CenterMark.LINK -> R.string.style_center_link
        CenterMark.WIFI -> R.string.style_center_wifi
        CenterMark.PERSON -> R.string.style_center_person
        CenterMark.EMAIL -> R.string.style_center_email
        CenterMark.PHONE -> R.string.style_center_phone
        CenterMark.SMS -> R.string.style_center_sms
        CenterMark.CRYPTO -> R.string.style_center_crypto
        CenterMark.GITHUB -> R.string.style_center_github
        CenterMark.MASTODON -> R.string.style_center_mastodon
        CenterMark.MATRIX -> R.string.style_center_matrix
        CenterMark.GLOBE -> R.string.style_center_globe
        CenterMark.AUTO -> R.string.style_center_auto
    }

    fun presetLabelRes(id: String): Int = when (id) {
        "classic" -> R.string.style_preset_classic
        "ink" -> R.string.style_preset_ink
        "ocean" -> R.string.style_preset_ocean
        "forest" -> R.string.style_preset_forest
        "oled" -> R.string.style_preset_oled
        "sunset" -> R.string.style_preset_sunset
        else -> R.string.style_preset_custom
    }

    fun wifiSecurityLabelRes(security: WifiSecurity): Int = when (security) {
        WifiSecurity.NOPASS -> R.string.style_wifi_open
        WifiSecurity.WEP -> R.string.style_wifi_wep
        WifiSecurity.WPA -> R.string.style_wifi_wpa
    }

    fun selectedPresetId(style: QrStyle): String? = StylePresets.ALL.firstOrNull { pack ->
        val p = pack.style
        p.moduleShape == style.moduleShape &&
            p.finderShape == style.finderShape &&
            p.finderPupil == style.finderPupil &&
            p.colorTheme == style.colorTheme &&
            p.frame == style.frame &&
            p.foregroundArgb == style.foregroundArgb &&
            p.eyeColorArgb == style.eyeColorArgb &&
            p.backgroundArgb == style.backgroundArgb
    }?.id
}
