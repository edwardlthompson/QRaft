package org.qraft.app.ui.editor

internal enum class ExportPick {
    Png, Svg, Pdf, Print, StyleQr,
    Json, Zip, ImportJson, CopyPayload,
    Duplicate, New, AddWidget, Wallpaper,
}

internal enum class ExportGroup { Takeaway, Library, Draft }

internal object ExportMenu {
    fun groupOf(pick: ExportPick): ExportGroup = when (pick) {
        ExportPick.Png, ExportPick.Svg, ExportPick.Pdf, ExportPick.Print, ExportPick.StyleQr ->
            ExportGroup.Takeaway
        ExportPick.Json, ExportPick.Zip, ExportPick.ImportJson, ExportPick.CopyPayload ->
            ExportGroup.Library
        ExportPick.Duplicate, ExportPick.New, ExportPick.AddWidget, ExportPick.Wallpaper ->
            ExportGroup.Draft
    }

    fun home(): List<ExportPick> = listOf(
        ExportPick.Png, ExportPick.Svg, ExportPick.Pdf, ExportPick.Print, ExportPick.StyleQr,
        ExportPick.Json, ExportPick.Zip, ExportPick.ImportJson,
        ExportPick.Duplicate, ExportPick.New, ExportPick.AddWidget,
    )

    fun gallery(): List<ExportPick> = listOf(
        ExportPick.Png, ExportPick.Svg, ExportPick.Pdf, ExportPick.Print,
        ExportPick.Json, ExportPick.Zip, ExportPick.CopyPayload,
        ExportPick.AddWidget,
    )

    fun grouped(picks: List<ExportPick>): List<Pair<ExportGroup, List<ExportPick>>> =
        ExportGroup.entries.map { group -> group to picks.filter { groupOf(it) == group } }
            .filter { it.second.isNotEmpty() }
}
