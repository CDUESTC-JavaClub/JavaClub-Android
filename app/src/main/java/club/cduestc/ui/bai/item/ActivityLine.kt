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
import androidx.cardview.widget.CardView
import club.byjh.entity.activity.Activity
import club.cduestc.R
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager
import java.text.SimpleDateFormat
import java.util.*

class ActivityLine(context: Context,
                   var signed : Boolean,
                   private val app : AppCompatActivity,
                   val activity : Activity,
                   private val onClick : OnClickListener
) : LinearLayout(context) {
    init {
        this.setOnClickListener(onClick)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val view: View = LayoutInflater.from(context).inflate(R.layout.bai_activity_line, this)
        view.findViewById<TextView>(R.id.bai_activity_name).text = activity.name
        view.findViewById<TextView>(R.id.bai_activity_local).text = activity.place
        val statusText = view.findViewById<TextView>(R.id.bai_text_status)
        statusText.text = "(${activity.status})"
        statusText.setTextColor(statusColor(activity.status))

        view.findViewById<TextView>(R.id.bai_activity_time).text = format.format(activity.start)
        view.findViewById<TextView>(R.id.bai_text_type).text = activity.type
        val image = when(activity.type){
            "博学" -> R.drawable.bai_icon_bx
            "笃行" -> R.drawable.bai_icon_dx
            "尽美" -> R.drawable.bai_icon_jm
            "明德" -> R.drawable.bai_icon_md
            else -> R.drawable.bai_icon_bx
        }
        val i = view.findViewById<ImageView>(R.id.bai_image_type)
        i.setImageResource(image)
        val color = when(activity.type){
            "博学" -> "#AAAF4C4C"
            "笃行" -> "#AA4CAF50"
            "尽美" -> "#AAFFC107"
            "明德" -> "#AA078BFF"
            else -> "#AAAF4C4C"
        }
        i.imageTintList = ColorStateList.valueOf(Color.parseColor(color))
        NetManager.createTask{
            val bitmap = UserManager.getHttpBitmap(activity.coverUrl)
            app.runOnUiThread {
                view.findViewById<ImageView>(R.id.bai_activity_header).setImageBitmap(bitmap)
            }
        }
    }

    private fun statusColor(status : String) : Int{
        return when(status){
            "报名中" -> Color.parseColor("#4CAF50")
            "活动中" -> Color.parseColor("#FFC107")
            "报名结束" -> Color.parseColor("#F44336")
            else -> Color.GRAY
        }
    }
}