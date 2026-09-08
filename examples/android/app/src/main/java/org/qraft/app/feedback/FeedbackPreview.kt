package org.qraft.app.feedback

import org.qraft.app.privacyreport.ReportMarkdown
import org.qraft.app.privacyreport.SanitizeReport

object FeedbackPreview {
    fun text(kind: String, description: String?, stack: String?): String =
        ReportMarkdown.build(kind = kind, description = description, stack = stack)

    fun canSubmit(description: String?, stack: String?): Boolean =
        SanitizeReport.text(description).isNotBlank() || SanitizeReport.text(stack, stack = true).isNotBlank()
}
