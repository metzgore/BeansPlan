package de.metzgore.beansplan.about

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import android.webkit.WebView
import de.metzgore.beansplan.R

class OssLicenseDialog : AppCompatDialogFragment() {

    companion object {
        val TAG = OssLicenseDialog::class.java.simpleName
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val webView = WebView(requireContext())
        webView.loadUrl("file:///android_asset/licenses.html")

        return AlertDialog.Builder(requireContext())
                .apply {
                    setTitle(getString(R.string.activity_about_open_source_licenses_title))
                    setView(webView)
                    setPositiveButton(android.R.string.ok
                    ) { dialog, _ -> dialog.dismiss() }
                }
                .create()
    }
}
