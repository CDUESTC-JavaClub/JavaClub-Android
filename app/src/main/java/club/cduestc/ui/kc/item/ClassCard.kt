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
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSONArray
import androidx.core.content.ContextCompat.startActivity

import club.cduestc.MainActivity

import android.content.Intent
import androidx.core.content.ContextCompat
import club.cduestc.R.id.card_body_parent
import club.cduestc.ui.kc.sub.KcTableActivity


class ClassCard(
    context: Context?,
    val clazz: JSONObject?,
    private val time : String?,
    private val color: String?,
    private val activity: AppCompatActivity
) : LinearLayout(context) {

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.class_card, this)
        val name = view.findViewById<TextView>(R.id.card_name)
        if(this.clazz != null && this.time != null)
            name.text = "${this.time}" +
                    "\n${this.clazz.getString("name")}" +
                    "\n${this.clazz.getString("local")}" +
                    "\n${this.clazz.getString("teacher")}"
        val body = view.findViewById<LinearLayout>(card_body)
        if(color != null) {
            body.setBackgroundColor(Color.parseColor(this.color))
        }else{
            view.findViewById<View>(R.id.card_empty).visibility = View.VISIBLE
            view.findViewById<CardView>(card_body_parent).visibility = View.GONE
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
        view.findViewById<Button>(R.id.class_hide).setOnClickListener { this.hideClass(this.clazz.getString("name")) }
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams
        dialog.show()
    }

    private fun hideClass(name : String){
        val sharedPreference = context.getSharedPreferences("class_table", AppCompatActivity.MODE_PRIVATE)
        val list = JSONArray.parseArray(sharedPreference.getString("ignore", "[]"))
        list.add(name)
        sharedPreference.edit().putString("ignore", list.toString()).apply()
        activity.finish()
        val intent = Intent(context, KcTableActivity::class.java)
        context.startActivity(intent)
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