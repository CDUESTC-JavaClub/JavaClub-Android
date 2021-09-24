package club.cduestc.ui.bai.sub

import android.app.AlertDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
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
                findViewById<ProgressBar>(R.id.loadActivity).visibility = View.GONE
            }
        }
    }

    private fun detail(v : View){
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        v as ActivityLine
        val builder: AlertDialog.Builder = AlertDialog.Builder(this,R.style.Translucent_NoTitle);
        val view: View = LayoutInflater.from(this).inflate(R.layout.bai_activity_line_detail, null)
        NetManager.createTask{
            val bitmap = UserManager.getHttpBitmap(v.activity.coverUrl)
            this.runOnUiThread {
                view.findViewById<ImageView>(R.id.activity_avatar).setImageBitmap(bitmap)
            }
        }
        view.findViewById<TextView>(R.id.activity_name).text = v.activity.name
        view.findViewById<TextView>(R.id.bai_activity_local).text = v.activity.place
        view.findViewById<TextView>(R.id.activity_desc).text = "报名人数：${v.activity.reg}/${v.activity.max}"
        view.findViewById<TextView>(R.id.bai_activity_time).text = format.format(v.activity.start)
        val dialog = builder.setView(view).create()
        dialog.show()
    }

    private fun filter(v : View){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, android.R.style.ThemeOverlay_Material_Dialog)
        builder.setTitle("请选择筛选的活动类型")
        val types = listOf("全部", "尽美", "博学" ,"明德", "笃行").toTypedArray()
        builder.setItems(types) { _, which ->
            type = if(which != 0) types[which] else ""
            findViewById<ProgressBar>(R.id.loadActivity).visibility = View.VISIBLE
            init()
        }
        builder.show()
    }
}