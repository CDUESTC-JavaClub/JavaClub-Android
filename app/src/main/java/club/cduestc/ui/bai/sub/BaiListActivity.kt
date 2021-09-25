package club.cduestc.ui.bai.sub

import android.app.AlertDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import club.byjh.entity.activity.SignedActivity
import club.byjh.exception.ActivityOprException
import club.byjh.net.WebManager
import club.byjh.net.enums.StatusType
import club.cduestc.R
import club.cduestc.ui.bai.item.ActivityLine
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors

class BaiListActivity : AppCompatActivity() {

    var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bai_list)
        init()
        findViewById<Button>(R.id.btnFilterActivity).setOnClickListener(this::filter)
    }

    private fun init(){
        val menu = findViewById<LinearLayout>(R.id.bai_activity_list)
        menu.removeAllViews()
        NetManager.createTask{
            val list = WebManager
                .getAllActivities(StatusType.ALL)
                .stream()
                .filter { it.type.contains(type) }
                .limit(50)
                .collect(Collectors.toList())
            this.runOnUiThread{
                list.forEach{menu.addView(ActivityLine(this, this, it, this::detail))}
                findViewById<View>(R.id.loadActivity).visibility = View.GONE
            }
        }
    }

    private fun detail(v : View){
        findViewById<View>(R.id.loadActivity).visibility = View.VISIBLE
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        v as ActivityLine
        val builder: AlertDialog.Builder = AlertDialog.Builder(this,R.style.Translucent_NoTitle)
        val view: View = LayoutInflater.from(this).inflate(R.layout.bai_activity_line_detail, null)
        val dialog = builder.setView(view).create()
        NetManager.createTask{
            val desc = UserManager.baiAccount.getActivityDesc(v.activity.id)
            val bitmap = UserManager.getHttpBitmap(v.activity.coverUrl)
            this.runOnUiThread {
                view.findViewById<ImageView>(R.id.activity_avatar).setImageBitmap(bitmap)
                view.findViewById<TextView>(R.id.activity_desc).text = Html.fromHtml(desc)
                findViewById<View>(R.id.loadActivity).visibility = View.GONE
                dialog.show()
            }
        }
        view.findViewById<TextView>(R.id.activity_name).text = v.activity.name
        view.findViewById<TextView>(R.id.bai_activity_local).text = "活动地点："+v.activity.place
        view.findViewById<TextView>(R.id.activity_status).text = "当前状态："+v.activity.status
        view.findViewById<TextView>(R.id.activity_count).text = "报名人数：${v.activity.reg}/${v.activity.max}"
        view.findViewById<TextView>(R.id.bai_activity_time).text = "活动时间："+format.format(v.activity.start)
        val btn = view.findViewById<Button>(R.id.activity_btn)
        btn.setTextColor(Color.parseColor("#A8A8A8"))
        if(v.activity is SignedActivity){
            btn.isEnabled = true
            btn.setTextColor(Color.parseColor("#F44336"))
            btn.text = "取消参加此活动"
            btn.setOnClickListener {
                dialog.dismiss()
                NetManager.createTask{
                    try{
                        UserManager.baiAccount.cancelActivity(v.activity.id)
                        this.runOnUiThread{ Toast.makeText(this, "取消报名成功！", Toast.LENGTH_LONG).show() }
                    }catch (e : ActivityOprException){
                        this.runOnUiThread{ Toast.makeText(this, e.message, Toast.LENGTH_LONG).show() }
                    }
                }
            }
        }else{
            when(v.activity.status){
                "报名中" -> {
                    btn.isEnabled = true
                    btn.setTextColor(Color.parseColor("#4CAF50"))
                    btn.text = "报名参加此活动"
                    btn.setOnClickListener {
                        dialog.dismiss()
                        NetManager.createTask{
                            try{
                                UserManager.baiAccount.signActivity(v.activity.id)
                                this.runOnUiThread{ Toast.makeText(this, "报名成功！", Toast.LENGTH_LONG).show() }
                            }catch (e : ActivityOprException){
                                this.runOnUiThread{ Toast.makeText(this, e.message, Toast.LENGTH_LONG).show() }
                            }
                        }
                    }
                }
                "活动中" -> {
                    btn.isEnabled = false
                    btn.text = "活动正在进行中"
                }
                else -> {
                    btn.isEnabled = false
                    btn.text = "活动已结束"
                }
            }
        }
    }

    private fun filter(v : View){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, android.R.style.ThemeOverlay_Material_Dialog)
        builder.setTitle("请选择筛选的活动类型")
        val types = listOf("全部", "尽美", "博学" ,"明德", "笃行").toTypedArray()
        builder.setItems(types) { _, which ->
            type = if(which != 0) types[which] else ""
            findViewById<View>(R.id.loadActivity).visibility = View.VISIBLE
            init()
        }
        builder.show()
    }
}