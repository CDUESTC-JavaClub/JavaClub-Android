package club.cduestc.ui.bai.item

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import club.byjh.entity.activity.Activity
import club.byjh.entity.activity.SignedActivity
import club.cduestc.R
import java.text.SimpleDateFormat
import java.util.*

class MyActivityLine(context: Context,
                     private val app : AppCompatActivity,
                     val activity : SignedActivity,
                     private val onClick : OnClickListener
) : LinearLayout(context) {
    init {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val view: View = LayoutInflater.from(context).inflate(R.layout.bai_activity_my_line, this)
        view.findViewById<TextView>(R.id.bai_activity_name).text = activity.name
        view.findViewById<TextView>(R.id.bai_activity_local).text = activity.place
        view.findViewById<TextView>(R.id.bai_activity_time).text = format.format(activity.start)
        view.findViewById<ImageView>(R.id.qrcode_img).setOnClickListener(onClick)
    }
}