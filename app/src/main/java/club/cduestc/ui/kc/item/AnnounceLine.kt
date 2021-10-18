package club.cduestc.ui.kc.item

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import club.cduestc.R
import com.alibaba.fastjson.JSONObject

class AnnounceLine(context: Context,
                   obj : JSONObject
) : LinearLayout(context) {

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.announce_line, this)
        view.findViewById<TextView>(R.id.title).text = obj.getString("title")
        view.findViewById<TextView>(R.id.time).text = obj.getString("time")
        this.setOnClickListener {
            val uri: Uri = Uri.parse("http://www.cduestc.cn/"+obj.getString("href"))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }
    }
}