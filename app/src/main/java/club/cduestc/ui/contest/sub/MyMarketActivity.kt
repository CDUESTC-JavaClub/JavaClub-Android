package club.cduestc.ui.contest.sub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import club.cduestc.R
import club.cduestc.databinding.ActivityMainBinding
import club.cduestc.databinding.ActivityMyMarketBinding
import club.cduestc.databinding.FragmentContestBinding
import club.cduestc.ui.contest.item.MarketLine
import club.cduestc.util.AnimUtil
import club.cduestc.util.NetManager
import com.alibaba.fastjson.JSONObject

class MyMarketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyMarketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.myMarketControl.addTab("发布新商品", binding.addItem)
        binding.myMarketControl.addTab("管理个人商品", binding.myItem)

        binding.marketMyLoading.setOnClickListener {  }

        init()
    }

    private fun init(){
        NetManager.createTask{
            val data = NetManager.getNews("market-my")
            runOnUiThread {
                data?.forEach {
                    it as JSONObject
                    binding.myItemList.addView(MarketLine(this, it))
                }

                AnimUtil.hide(binding.marketMyLoading)
                AnimUtil.show(binding.myMarket, 0f, 1f)
                AnimUtil.show(binding.addItem, 0f, 1f)
            }
        }
    }
}