package club.cduestc.ui.contest.item

import android.app.Activity
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import club.cduestc.R
import com.alibaba.fastjson.JSONObject

class MarketLine(context: Activity, data : JSONObject) : ConstraintLayout(context) {

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.market_line, this)
        view.findViewById<TextView>(R.id.market_item_name).text = data.getString("name")
    }
}