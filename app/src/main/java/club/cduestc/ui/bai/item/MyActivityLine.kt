package club.cduestc.ui.bai.item

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
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
        view.findViewById<TextView>(R.id.type_text).text = when(activity.type){
            "博学" -> context.getString(R.string.bai_name_bx)
            "笃行" -> context.getString(R.string.bai_name_dx)
            "尽美" -> context.getString(R.string.bai_name_jm)
            "明德" -> context.getString(R.string.bai_name_md)
            else -> context.getString(R.string.bai_name_bx)
        }
        val id = when(activity.type){
            "笃行" -> R.drawable.bai_icon_dx
            "博学" -> R.drawable.bai_icon_bx
            "明德" -> R.drawable.bai_icon_md
            else -> R.drawable.bai_icon_jm
        }
        val img = view.findViewById<ImageView>(R.id.type_img)
        img.setImageResource(id)
        val color = when(activity.type){
            "博学" -> "#AAAF4C4C"
            "笃行" -> "#AA4CAF50"
            "尽美" -> "#AAFFC107"
            "明德" -> "#AA078BFF"
            else -> "#AAAF4C4C"
        }
        img.imageTintList = ColorStateList.valueOf(Color.parseColor(color))
    }
}