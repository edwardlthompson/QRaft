package org.qraft.app.editor

import android.content.Context

object DraftStore {
    private const val PREFS = "qraft_draft"
    private const val KIND = "kind"
    private const val PRIMARY = "primary"
    private const val SECONDARY = "secondary"
    private const val TERTIARY = "tertiary"
    private const val WIFI = "wifi"
    private const val HIDDEN = "hidden"
    private const val ORG = "org"
    private const val URL = "url"
    private const val STYLE = "style"
    private const val NAME = "name"

    fun save(context: Context, draft: EditorDraft, styleJson: String, saveName: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KIND, draft.kind.name)
            .putString(PRIMARY, draft.primary)
            .putString(SECONDARY, draft.secondary)
            .putString(TERTIARY, draft.tertiary)
            .putString(WIFI, draft.wifiSecurity.name)
            .putBoolean(HIDDEN, draft.wifiHidden)
            .putString(ORG, draft.org)
            .putString(URL, draft.url)
            .putString(STYLE, styleJson)
            .putString(NAME, saveName)
            .apply()
    }

    fun loadDraft(context: Context): EditorDraft? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val primary = prefs.getString(PRIMARY, null) ?: return null
        return EditorDraft.fromParts(
            prefs.getString(KIND, PayloadKind.Url.name) ?: PayloadKind.Url.name,
            primary,
            prefs.getString(SECONDARY, "") ?: "",
            prefs.getString(TERTIARY, "") ?: "",
            prefs.getString(WIFI, "") ?: "",
            wifiHidden = prefs.getBoolean(HIDDEN, false),
            org = prefs.getString(ORG, "") ?: "",
            url = prefs.getString(URL, "") ?: "",
        )
    }

    fun loadStyleJson(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(STYLE, "") ?: ""

    fun loadName(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(NAME, "Website") ?: "Website"
}
