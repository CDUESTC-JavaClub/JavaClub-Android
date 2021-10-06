package club.cduestc.ui.contest.item

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import club.cduestc.R
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager
import com.alibaba.fastjson.JSONObject

class MarketCard(context: Activity, data : JSONObject) : ConstraintLayout(context) {
    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.market_card, this)
        view.findViewById<TextView>(R.id.market_item_name).text = data.getString("name")
        view.findViewById<TextView>(R.id.market_item_price).text = data.getDouble("price").toString()

        val type = data.getString("type")
        if(type == "出售"){
            view.findViewById<TextView>(R.id.market_item_type_color).backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#2196F3"))
            view.findViewById<TextView>(R.id.market_item_type_name).text = type
        }else{
            view.findViewById<TextView>(R.id.market_item_type_color).backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#BB86FC"))
            view.findViewById<TextView>(R.id.market_item_type_name).text = type
        }

        NetManager.createTask{
            val url = data.getJSONArray("images").getString(0)
            val bitmap = NetManager.getHttpBitmap(url)
            if(bitmap != null){
                val value = bitmap.height.toFloat()/bitmap.width.toFloat()
                context.runOnUiThread {
                    val img = findViewById<ImageView>(R.id.market_item_img)
                    img.layoutParams.height = (measuredWidth * value).toInt()
                    img.setImageBitmap(bitmap)
                }
            }
        }
    }
}