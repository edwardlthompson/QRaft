package org.qraft.app.share

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient

/** System print helper for a local HTML page embedding the QR PNG as a data URL is overkill —
 *  print a simple WebView of SVG/text caption instead. */
object QrPrint {
    fun printSvg(context: Context, jobName: String, svg: String) {
        val web = WebView(context)
        web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val mgr = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                val adapter = web.createPrintDocumentAdapter(jobName)
                mgr.print(
                    jobName,
                    adapter,
                    PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build(),
                )
            }
        }
        val html = "<html><body>$svg</body></html>"
        web.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }
}
