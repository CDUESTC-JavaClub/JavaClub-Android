package club.cduestc.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import club.cduestc.R


object UpdateUtil {

    private const val version = "1.0.0"

    fun getVersion() : String{
        return version
    }

    fun showUpdateDialog(context: Context, url : String, version : String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.Translucent_NoTitle)
        val view: View = LayoutInflater.from(context).inflate(R.layout.update_dialog, null)
        val btn = view.findViewById<Button>(R.id.btn_update)
        view.findViewById<TextView>(R.id.version_text).text = context.getString(R.string.update_version, this.version, version)
        val dialog = builder.setView(view).create()
        btn.setOnClickListener {
            dialog.dismiss()
            val uri: Uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }
        dialog.show()
    }
}