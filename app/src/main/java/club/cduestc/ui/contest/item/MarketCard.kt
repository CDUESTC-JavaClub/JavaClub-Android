package club.cduestc.ui.contest.item

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import club.cduestc.R
import club.cduestc.ui.contest.sub.MarketDetailActivity
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager
import com.alibaba.fastjson.JSONObject

class MarketCard(context: Activity, data : JSONObject) : ConstraintLayout(context) {
    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.market_card, this)
        view.findViewById<TextView>(R.id.market_item_name).text = data.getString("name")
        view.findViewById<TextView>(R.id.market_item_price).text = "￥${data.getDouble("price")}"

        val type = data.getString("type")
        if(type == "出售"){
            view.findViewById<View>(R.id.market_item_type_color).backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#2196F3"))
            view.findViewById<TextView>(R.id.market_item_type_name).text = type
        }else{
            view.findViewById<View>(R.id.market_item_type_color).backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#BB86FC"))
            view.findViewById<TextView>(R.id.market_item_type_name).text = type
        }

        this.setOnClickListener {
            val intent = Intent(context, MarketDetailActivity::class.java)
            intent.putExtra("data", data.toJSONString())
            context.startActivity(intent)
        }

        NetManager.createTask{
            val arr = data.getJSONArray("images")
            if(arr.size > 0){
                val url = data.getJSONArray("images").getString(0)
                val bitmap = NetManager.getHttpBitmap(url)
                if(bitmap != null){
                    context.runOnUiThread {
                        val img = findViewById<ImageView>(R.id.market_item_img)
                        img.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }
}