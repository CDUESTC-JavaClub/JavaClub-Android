package club.cduestc.ui.contest.item

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import club.cduestc.R
import com.alibaba.fastjson.JSONObject

class MarketCard(context: Activity, data : JSONObject) : ConstraintLayout(context) {
    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.contest_line, this)

    }
}