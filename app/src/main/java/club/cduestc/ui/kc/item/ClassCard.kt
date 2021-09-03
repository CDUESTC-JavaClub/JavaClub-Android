package club.cduestc.ui.kc.item

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import club.cduestc.R
import club.cduestc.R.id.card_body
import com.alibaba.fastjson.JSONObject
import android.view.WindowManager
import com.alibaba.fastjson.JSONArray


class ClassCard(
    context: Context?,
    private val clazz: JSONObject?,
    private val time : String?,
    private val color: String?
) : LinearLayout(context) {

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.class_card, this)
        val name = view.findViewById<TextView>(R.id.card_name)
        if(this.clazz != null && this.time != null)
            name.text = "${this.time}" +
                    "\n${this.clazz.getString("name")}" +
                    "\n${this.clazz.getString("local")}" +
                    "\n${this.clazz.getString("teacher")}"
        val body = view.findViewById<CardView>(card_body)
        if(color != null) {
            body.backgroundTintList = ColorStateList.valueOf(Color.parseColor(this.color))
        }else{
            view.findViewById<View>(R.id.card_empty).visibility = View.VISIBLE
            view.findViewById<CardView>(card_body).visibility = View.GONE
        }
        this.setOnClickListener(this::onClick)
    }

    private fun onClick(it : View){
        if(clazz == null) return
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val view: View = LayoutInflater.from(context).inflate(R.layout.class_card_detail, null)
        val text = view.findViewById<TextView>(R.id.class_info)
        text.text = "课程名称：${this.clazz.getString("name")}\n" +
                "任课老师：${this.clazz.getString("teacher")}\n" +
                "上课时间：周 ${this.clazz.getString("day")} 第 ${this.clazz.getJSONArray("indexSet")} 节\n" +
                "上课地点：${this.clazz.getString("local")}\n" +
                "周数：${convertWeek(this.clazz.getJSONArray("weekSet"))}\n"
        val dialog = builder.setView(view).create()
        view.findViewById<Button>(R.id.class_ok).setOnClickListener { dialog.dismiss() }
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams
        dialog.show()
    }

    private fun convertWeek(week : JSONArray) : String{
        val f = week[0] as Int
        return if(week.contains(f+1)) {
            "${week[0]}-${week.last()}"
        }else{
            if(f % 2 == 0){
                "${week[0]}-${week.last()}（双）"
            }else{
                "${week[0]}-${week.last()}（单）"
            }
        }
    }
}