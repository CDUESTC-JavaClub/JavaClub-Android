package club.cduestc.ui.contest.sub

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import club.cduestc.R
import club.cduestc.databinding.ActivityJobBinding
import club.cduestc.databinding.ActivityMarketDetailBinding
import com.alibaba.fastjson.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class JobActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

        val data : JSONObject = JSONObject.parseObject(intent.getSerializableExtra("data") as String)
        binding.jobName.text = data.getString("name")
        binding.jobLocal.text = "位置："+data.getString("local")
        binding.jobHost.text = "发布单位："+data.getString("host")
        binding.jobTime.text = "发布时间："+format.format(data.getDate("time"))
        binding.jobSalaryUnit.text = " / "+data.getString("unit")

        val max = data.getInteger("max_salary")
        val min = data.getInteger("min_salary")
        binding.jobSalaryValue.text = if (max != min){
            "$min-$max"
        }else{
            max.toString()
        }

        binding.jobDesc.text = data.getString("desc")

        binding.jobTypeName.text = data.getString("type")
        binding.jobTypeColor.backgroundTintList = when(data.getString("type")){
            "周末" -> ColorStateList.valueOf(Color.parseColor("#FFC107"))
            "长期" -> ColorStateList.valueOf(Color.parseColor("#2196F3"))
            else -> ColorStateList.valueOf(Color.parseColor("#BB86FC"))
        }

    }
}