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
import club.cduestc.util.AnimUtil
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors
import kotlin.streams.toList

class BaiListActivity : AppCompatActivity() {

    var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bai_list)
        findViewById<View>(R.id.loadActivity).setOnClickListener {  }
        init()
        findViewById<Button>(R.id.btnFilterActivity).setOnClickListener(this::filter)
    }

    private fun init(){
        val menu = findViewById<LinearLayout>(R.id.bai_activity_list)
        menu.removeAllViews()
        NetManager.createTask{
            val myList = UserManager.baiAccount.activities.stream().map { it.id }.toList()
            val list = WebManager
                .getAllActivities(StatusType.ALL)
                .stream()
                .filter { it.type.contains(type) }
                .limit(50)
                .collect(Collectors.toList())
            this.runOnUiThread{
                list.forEach{menu.addView(ActivityLine(this, myList.contains(it.id),this, it, this::detail))}
                AnimUtil.hide(findViewById(R.id.loadActivity))
            }
        }
    }

    private fun detail(v : View){
        AnimUtil.show(findViewById(R.id.loadActivity), 0f, 1f)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        v as ActivityLine
        val builder: AlertDialog.Builder = AlertDialog.Builder(this,R.style.Translucent_NoTitle)
        val view: View = LayoutInflater.from(this).inflate(R.layout.bai_activity_line_detail, null)
        val dialog = builder.setView(view).create()
        NetManager.createTask{
            val desc = UserManager.baiAccount.getActivityDesc(v.activity.id)
            val bitmap = NetManager.getHttpBitmap(v.activity.coverUrl)
            this.runOnUiThread {
                view.findViewById<ImageView>(R.id.activity_avatar).setImageBitmap(bitmap)
                view.findViewById<TextView>(R.id.activity_desc).text = Html.fromHtml(desc)
                findViewById<View>(R.id.loadActivity).visibility = View.GONE
                dialog.show()
            }
        }
        view.findViewById<TextView>(R.id.activity_name).text = v.activity.name
        view.findViewById<TextView>(R.id.bai_activity_local).text = getString(R.string.bai_activity_detail_local, v.activity.place)
        view.findViewById<TextView>(R.id.activity_status).text = getString(R.string.bai_activity_detail_status, v.activity.status)
        view.findViewById<TextView>(R.id.activity_count).text = getString(R.string.bai_activity_detail_people, v.activity.reg, v.activity.max)
        view.findViewById<TextView>(R.id.bai_activity_time).text = getString(R.string.bai_activity_detail_time, format.format(v.activity.start))
        val btn = view.findViewById<Button>(R.id.activity_btn)
        btn.setTextColor(Color.parseColor("#A8A8A8"))
        if(v.signed){
            btn.isEnabled = true
            btn.setTextColor(Color.parseColor("#F44336"))
            btn.text = getString(R.string.bai_activity_detail_cancel)
            btn.setOnClickListener {
                dialog.dismiss()
                NetManager.createTask{
                    try{
                        UserManager.baiAccount.cancelActivity(v.activity.id)
                        this.runOnUiThread{
                            Toast.makeText(this, getString(R.string.bai_activity_cancel_success), Toast.LENGTH_LONG).show()
                            v.signed = false
                        }
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
                    btn.text = getString(R.string.bai_activity_detail_join)
                    btn.setOnClickListener {
                        dialog.dismiss()
                        NetManager.createTask{
                            try{
                                UserManager.baiAccount.signActivity(v.activity.id)
                                this.runOnUiThread{
                                    Toast.makeText(this, getString(R.string.bai_activity_join_success), Toast.LENGTH_LONG).show()
                                    v.signed = true
                                }
                            }catch (e : ActivityOprException){
                                this.runOnUiThread{ Toast.makeText(this, e.message, Toast.LENGTH_LONG).show() }
                            }
                        }
                    }
                }
                "活动中" -> {
                    btn.isEnabled = false
                    btn.text = getString(R.string.bai_activity_detail_running)
                }
                "报名结束" ->{
                    btn.isEnabled = false
                    btn.text = getString(R.string.bai_activity_detail_stop)
                }
                else -> {
                    btn.isEnabled = false
                    btn.text = getString(R.string.bai_activity_detail_end)
                }
            }
        }
    }

    private fun filter(v : View){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, android.R.style.ThemeOverlay_Material_Dialog)
        builder.setTitle(getString(R.string.bai_activity_filter))
        val names = listOf(getString(R.string.bai_name_all), getString(R.string.bai_name_jm),
            getString(R.string.bai_name_bx) ,getString(R.string.bai_name_md),
            getString(R.string.bai_name_dx)).toTypedArray()
        val types = listOf("全部", "尽美", "博学" ,"明德", "笃行").toTypedArray()
        builder.setItems(names) { _, which ->
            type = if(which != 0) types[which] else ""
            val view = findViewById<View>(R.id.loadActivity)
            view.alpha = 1f
            AnimUtil.show(view)
            init()
        }
        builder.show()
    }
}