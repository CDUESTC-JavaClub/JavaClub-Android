package club.cduestc.ui.kc.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import club.cduestc.R

class KcClassWidgetLine(context: Context?,
                        private val name : String,
                        private val desc : String,
                        private val time : String
 ) : LinearLayout(context) {
    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.class_line, this)
        view.findViewById<TextView>(R.id.widget_class_time).text = time
    }


}