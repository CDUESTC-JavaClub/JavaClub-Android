package club.cduestc.ui.kc.item

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import club.cduestc.R

class InfoLine(context: Context,
               key : String,
               value : String
) : LinearLayout(context) {

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.info_line, this)
        view.findViewById<TextView>(R.id.kc_details_key).text = key
        view.findViewById<TextView>(R.id.kc_details_value).text = value
    }
}