package club.cduestc.ui.contest.item

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import club.cduestc.R
import club.cduestc.ui.CollapseCardView
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager
import com.alibaba.fastjson.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ContestLine(context: Activity, data : JSONObject, other : List<ContestLine>) : LinearLayout(context) {
    init {
        println(data)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val view: View = LayoutInflater.from(context).inflate(R.layout.contest_line, this)
        setOnClickListener { findViewById<CollapseCardView>(R.id.time_line_card).toggle() }
        view.findViewById<TextView>(R.id.contest_name).text = data.getString("name")
        view.findViewById<TextView>(R.id.contest_time).text = format.format(data.getDate("time"))
        view.findViewById<TextView>(R.id.contest_depart).text = data.getString("depart")
        view.findViewById<TextView>(R.id.contest_desc).text = Html.fromHtml(data.getString("desc"))
        view.findViewById<Button>(R.id.btn_detail).setOnClickListener{
            val uri: Uri = Uri.parse(data.getString("url"))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }
        NetManager.createTask{
            val bitmap = UserManager.getHttpBitmap(data.getString("icon"))
            context.runOnUiThread { view.findViewById<ImageView>(R.id.contest_img).setImageBitmap(bitmap) }
        }
        view.findViewById<CollapseCardView>(R.id.time_line_card).setOnClickListener {
            other.forEach { line -> if(line != this) line.findViewById<CollapseCardView>(R.id.time_line_card).collapse() }
            it as CollapseCardView
            it.toggle()
        }
    }
}