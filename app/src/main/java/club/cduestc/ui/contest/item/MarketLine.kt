package club.cduestc.ui.contest.item

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import club.cduestc.R
import club.cduestc.ui.contest.sub.MyMarketActivity
import club.cduestc.util.NetManager
import com.alibaba.fastjson.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MarketLine(activity: MyMarketActivity, data : JSONObject) : ConstraintLayout(activity) {

    var itemId: Int
    var activity : MyMarketActivity

    init {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val view: View = LayoutInflater.from(context).inflate(R.layout.market_line, this)
        itemId = data.getInteger("id")
        this.activity = activity
        view.findViewById<TextView>(R.id.market_item_name).text = data.getString("name")
        view.findViewById<TextView>(R.id.market_item_time).text = format.format(data.getDate("time"))
        view.findViewById<TextView>(R.id.market_item_status_text).text = when(data.getInteger("status")){
            0 -> "待审核"
            1 -> "已上架"
            else -> "已下架"
        }
        val color = when(data.getInteger("status")){
            0 -> "#FFC107"
            1 -> "#4CAF50"
            else -> "#A8A8A8"
        }
        view.findViewById<View>(R.id.market_item_status_color).backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))

        val cancelBtn = view.findViewById<Button>(R.id.market_item_cancel)
        cancelBtn.setOnClickListener(this::cancelItem)
        if(data.getInteger("status") == 2) cancelBtn.isEnabled = false

        NetManager.createTask{
            val arr = data.getJSONArray("images")
            if(arr.size > 0){
                val url = data.getJSONArray("images").getString(0)
                val bitmap = NetManager.getHttpBitmap(url)
                if(bitmap != null){
                    activity.runOnUiThread {
                        view.findViewById<ImageView>(R.id.market_item_img).setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    private fun cancelItem(view: View){
        activity.waitLoading()
        NetManager.createTask{
            if(NetManager.cancelItem(itemId)){
                activity.runOnUiThread {
                    activity.init()
                    Toast.makeText(activity, "商品下架成功！", Toast.LENGTH_SHORT).show()
                    val flag = Intent()
                    flag.putExtra("reload", true)
                    activity.setResult(10, flag)
                }
            }else{
                activity.runOnUiThread {
                    activity.endLoading()
                    Toast.makeText(activity, "未知错误，下架失败！", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}