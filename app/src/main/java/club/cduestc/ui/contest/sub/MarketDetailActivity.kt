package club.cduestc.ui.contest.sub

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import club.cduestc.R
import club.cduestc.databinding.ActivityMarketDetailBinding
import club.cduestc.databinding.ActivityMyMarketBinding
import club.cduestc.util.AnimUtil
import club.cduestc.util.NetManager
import com.alibaba.fastjson.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MarketDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarketDetailBinding

    private lateinit var imageList : List<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

        imageList = listOf(binding.marketItemImg1, binding.marketItemImg2, binding.marketItemImg3)

        val obj : JSONObject = JSONObject.parseObject(intent.getSerializableExtra("data") as String)

        binding.marketDetailName.text = obj.getString("name")
        binding.marketDetailDesc.text = obj.getString("desc")
        binding.marketDetailTime.text = getString(R.string.market_time, format.format(obj.getDate("time")))
        binding.marketDetailPrice.text = getString(R.string.market_price, obj.getDouble("price").toString())
        val type = obj.getString("type")
        if(type == "出售"){
            binding.marketDetailTypeColor.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#2196F3"))
            binding.marketDetailTypeName.text = type
        }else{
            binding.marketDetailTypeColor.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#BB86FC"))
            binding.marketDetailTypeName.text = type
        }
        binding.marketDetailBtn.setOnClickListener {
            try {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin="+obj.getString("qq_link"))
                startActivity(intent)
            }catch (e : ActivityNotFoundException){
                Toast.makeText(this, getString(R.string.driect_no_app), Toast.LENGTH_LONG).show()
            }
        }

        NetManager.createTask{
            val images = obj.getJSONArray("images")
            for (i in 0 until images.size){
                val bitmap = NetManager.getHttpBitmap(images[i].toString())
                runOnUiThread {
                    imageList[i].setImageBitmap(bitmap)
                }
            }
            runOnUiThread {
                AnimUtil.hide(binding.marketDetailLoading)
                AnimUtil.show(binding.marketDetail)
            }
        }
    }
}