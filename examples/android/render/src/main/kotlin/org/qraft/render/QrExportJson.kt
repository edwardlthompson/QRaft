package org.qraft.render

import org.json.JSONObject

data class QrExportDocument(
    val version: Int = 1,
    val payloadText: String,
    val style: QrStyle = QrStyle.DEFAULT,
)

object QrExportJson {
    fun encode(doc: QrExportDocument): String {
        require(doc.payloadText.isNotBlank()) { "payloadText must not be blank" }
        return JSONObject().apply {
            put("version", doc.version)
            put("payloadText", doc.payloadText)
            put("style", JSONObject(QrStyleJson.encode(doc.style)))
        }.toString()
    }

    fun decode(json: String): QrExportDocument? {
        if (json.isBlank()) return null
        return try {
            val obj = JSONObject(json)
            val payload = obj.optString("payloadText")
            if (payload.isBlank()) return null
            val styleObj = obj.optJSONObject("style")
            QrExportDocument(
                version = obj.optInt("version", 1),
                payloadText = payload,
                style = if (styleObj == null) QrStyle.DEFAULT else QrStyleJson.decode(styleObj.toString()),
            )
        } catch (_: Exception) {
            null
        }
    }
}
