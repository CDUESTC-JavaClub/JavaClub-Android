package club.cduestc.ui.contest.sub

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import club.cduestc.R
import club.cduestc.databinding.ActivityMyMarketBinding
import club.cduestc.ui.contest.item.MarketLine
import club.cduestc.util.AnimUtil
import club.cduestc.util.NetManager
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class MyMarketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyMarketBinding

    private var uploadUrls = JSONArray()

    private lateinit var imagesList: List<ImageView>
    private lateinit var imagesMaskList : List<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.myMarketControl.addTab("发布新商品", binding.addItem)
        binding.myMarketControl.addTab("管理个人商品", binding.myItem)

        binding.marketMyLoading.setOnClickListener {  }

        imagesMaskList = listOf(binding.marketItemUploadImageMask1, binding.marketItemUploadImageMask2, binding.marketItemUploadImageMask3)
        imagesList = listOf(binding.marketItemUploadImage1, binding.marketItemUploadImage2, binding.marketItemUploadImage3)
        for(i in imagesList.indices){
            imagesList[i].setOnClickListener { uploadImage(i) }
        }

        init()

        binding.marketMyBtn.setOnClickListener{
            AnimUtil.show(binding.marketMyLoading)
            if(!publish()) AnimUtil.hide(binding.marketMyLoading)
        }
    }

    private fun publish() : Boolean{
        if(binding.marketMyName.text.isEmpty() || binding.marketMyName.text.length < 5) {
            Toast.makeText(this, "商品标题必须是5-20个字之间！", Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.marketMyDesc.text.isEmpty() || binding.marketMyDesc.text.length < 10) {
            Toast.makeText(this, "商品描述必须是10-100个字之间！", Toast.LENGTH_SHORT).show()
            return false
        }
        if(uploadUrls.isEmpty()) {
            Toast.makeText(this, "至少需要添加一张图片！", Toast.LENGTH_SHORT).show()
            return false
        }
        val price = binding.marketMyPrice.text.toString().toDouble()
        if(price < 1 || price > 10000) {
            Toast.makeText(this, "定价只能在1-10000元之间！", Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.marketMyQq.text.length < 5 || binding.marketMyQq.text.length > 15){
            Toast.makeText(this, "请输入正确的QQ号码！", Toast.LENGTH_SHORT).show()
            return false
        }
        val type = if(binding.radio.isChecked){
            "SALE"
        }else{
            "BUY"
        }
        NetManager.createTask{
            runOnUiThread { AnimUtil.show(binding.marketMyLoading, 0f, 1f) }
            if(NetManager.createItem(binding.marketMyName.text.toString(), binding.marketMyDesc.text.toString(),
                uploadUrls.toJSONString(), binding.marketMyQq.text.toString(), price, type)){
                runOnUiThread {
                    Toast.makeText(this, "发布成功，审核完成后就能出现在列表啦！", Toast.LENGTH_LONG).show()
                    finish()
                }
            }else{
                runOnUiThread { Toast.makeText(this, "未知错误，发布失败！", Toast.LENGTH_SHORT).show() }
            }
            runOnUiThread { AnimUtil.hide(binding.marketMyLoading) }
        }
        return true
    }

    fun init(){
        binding.myItemList.removeAllViews()
        NetManager.createTask{
            val data = NetManager.getNews("market-my")
            runOnUiThread {
                data?.forEach {
                    it as JSONObject
                    binding.myItemList.addView(MarketLine(this, it))
                }

                AnimUtil.hide(binding.marketMyLoading)
                if(binding.myMarket.visibility != View.VISIBLE) AnimUtil.show(binding.myMarket, 0f, 1f)
                binding.myMarketControl.switchTab(binding.myMarketControl.selectIndex)
            }
        }
    }

    private fun uploadImage(i : Int){
        if(imagesMaskList[i].visibility != View.VISIBLE){
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            this.startActivityForResult(intent, i)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode > -1 && data != null){
            imagesMaskList[requestCode].visibility = View.VISIBLE
            val uri: Uri = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            imagesList[requestCode].setImageBitmap(bitmap)
            val resolver = this.contentResolver
            NetManager.createTask{
                val url = NetManager.uploadImage(resolver.openInputStream(uri))
                if(url.isNotEmpty()) {
                    try{
                        val obj = JSONObject.parseObject(url)
                        if(obj.getInteger("code") == 200){
                            uploadUrls.add(obj.getJSONObject("data").getJSONObject("url").getString("bilibili"))
                            runOnUiThread {
                                imagesMaskList[requestCode].visibility = View.GONE
                                if(requestCode < imagesList.size - 1){
                                    imagesList[requestCode+1].visibility = View.VISIBLE
                                }
                            }
                            return@createTask
                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
                runOnUiThread {
                    Toast.makeText(this, "图片上传失败！", Toast.LENGTH_SHORT).show()
                    imagesList[requestCode].setImageResource(R.drawable.market_add_image)
                    imagesMaskList[requestCode].visibility = View.GONE
                }
            }
        }
    }

    fun waitLoading(){
        AnimUtil.show(binding.marketMyLoading, 0f, 1f)
    }

    fun endLoading(){
        AnimUtil.hide(binding.marketMyLoading)
    }
}