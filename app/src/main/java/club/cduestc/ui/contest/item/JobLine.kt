package club.cduestc.ui.contest.item

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import club.cduestc.R
import club.cduestc.ui.contest.sub.JobActivity
import club.cduestc.ui.contest.sub.MarketDetailActivity
import com.alibaba.fastjson.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class JobLine(context: Activity, data : JSONObject)  : LinearLayout(context) {

    init {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val view: View = LayoutInflater.from(context).inflate(R.layout.job_line, this)
        view.findViewById<TextView>(R.id.job_name).text = data.getString("name")
        view.findViewById<TextView>(R.id.job_local).text = data.getString("local")
        view.findViewById<TextView>(R.id.job_time).text = format.format(data.getDate("time"))
        view.findViewById<TextView>(R.id.job_salary_unit).text = context.getString(R.string.job_unit, data.getString("unit"))

        val max = data.getInteger("max_salary")
        val min = data.getInteger("min_salary")
        view.findViewById<TextView>(R.id.job_salary_value).text = if (max != min){
            "$min-$max"
        }else{
            max.toString()
        }

        view.findViewById<TextView>(R.id.job_type_name).text = data.getString("type")
        view.findViewById<View>(R.id.job_type_color).backgroundTintList = when(data.getString("type")){
            "周末" -> ColorStateList.valueOf(Color.parseColor("#FFC107"))
            "长期" -> ColorStateList.valueOf(Color.parseColor("#2196F3"))
            else -> ColorStateList.valueOf(Color.parseColor("#BB86FC"))
        }

        setOnClickListener {
            val intent = Intent(context, JobActivity::class.java)
            intent.putExtra("data", data.toJSONString())
            context.startActivity(intent)
        }
    }
}