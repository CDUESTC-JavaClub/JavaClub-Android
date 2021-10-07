package club.cduestc.ui.contest.item

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import club.cduestc.R
import com.alibaba.fastjson.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class JobLine(context: Activity, data : JSONObject)  : LinearLayout(context) {

    init {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val view: View = LayoutInflater.from(context).inflate(R.layout.job_line, this)

    }
}