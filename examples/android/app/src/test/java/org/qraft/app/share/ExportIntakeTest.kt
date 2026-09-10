package org.qraft.app.share

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.qraft.app.editor.PayloadKind
import org.qraft.render.ModuleShape
import org.qraft.render.QrExportDocument
import org.qraft.render.QrExportJson
import org.qraft.render.QrStyle
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ExportIntakeTest {
    @Test
    fun validDocumentAppliesPayloadAndStyle() {
        val json = QrExportJson.encode(
            QrExportDocument(
                payloadText = "https://fdroid.org",
                style = QrStyle(moduleShape = ModuleShape.DIAMOND),
            ),
        )
        val applied = ExportIntake.apply(json)!!
        assertEquals(PayloadKind.Url, applied.first.kind)
        assertEquals("https://fdroid.org", applied.first.primary)
        assertEquals(ModuleShape.DIAMOND, applied.second.moduleShape)
    }

    @Test
    fun blankAndInvalidStayNull() {
        assertNull(ExportIntake.apply(null))
        assertNull(ExportIntake.apply(""))
        assertNull(ExportIntake.apply("not-json"))
        assertNull(ExportIntake.apply("[]"))
        assertNull(ExportIntake.apply("{\"version\":1}"))
    }

    @Test
    fun galleryBackupListIsNotADocument() {
        assertNull(ExportIntake.apply("[{\"id\":\"a\",\"name\":\"n\",\"payloadText\":\"https://x\"}]"))
    }
}
