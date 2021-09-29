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
    context: Context,
    val clazz: JSONObject?,
    private val time : String?,
    private val color: String?,
    private val activity: AppCompatActivity
) : LinearLayout(context) {

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.class_card, this)
        val name = view.findViewById<TextView>(R.id.card_name)
        if(this.clazz != null && this.time != null)
            name.text = context.getString(R.string.kc_class_card_table, this.time, this.clazz.getString("name"), this.clazz.getString("local"), this.clazz.getString("teacher"))
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
        val builder: AlertDialog.Builder = AlertDialog.Builder(context,R.style.Translucent_NoTitle)
        val view: View = LayoutInflater.from(context).inflate(R.layout.class_card_detail, null)
        view.findViewById<TextView>(R.id.class_info_name).text = this.clazz.getString("name")
        view.findViewById<TextView>(R.id.class_info_teacher).text = context.getString(R.string.kc_class_card_teacher, this.clazz.getString("teacher"))
        view.findViewById<TextView>(R.id.class_info_time).text = context.getString(R.string.kc_class_card_time, this.clazz.getString("day"), this.clazz.getJSONArray("indexSet"))
        view.findViewById<TextView>(R.id.class_info_local).text = context.getString(R.string.kc_class_card_local, this.clazz.getString("local"))
        view.findViewById<TextView>(R.id.class_info_week).text = context.getString(R.string.kc_class_card_week, convertWeek(this.clazz.getJSONArray("weekSet")))

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
                context.getString(R.string.kc_class_card_week_dual, week[0], week.last())
            }else{
                context.getString(R.string.kc_class_card_week_odd, week[0], week.last())
            }
        }
    }
}