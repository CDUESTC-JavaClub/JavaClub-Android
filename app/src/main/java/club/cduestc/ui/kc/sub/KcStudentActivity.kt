package club.cduestc.ui.kc.sub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import club.cduestc.R
import club.cduestc.ui.kc.item.InfoLine
import club.cduestc.util.UserManager
import java.text.SimpleDateFormat
import java.util.*

class KcStudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kc_student)

        initUserInfo()
    }

    private fun initUserInfo(){
        findViewById<TextView>(R.id.kc_details_header_name).text = UserManager.kcAccount.name
        findViewById<TextView>(R.id.kc_details_header_grade).text = "${UserManager.kcAccount.info.get("年级")}级 ${UserManager.kcAccount.info.get("院系")}"
        findViewById<TextView>(R.id.kc_details_header_local).text = UserManager.kcAccount.info.get("所属校区").toString()
        findViewById<TextView>(R.id.kc_details_header_type).text = "${UserManager.kcAccount.info.get("学习形式")}（${UserManager.kcAccount.info.get("学籍状态")}）"

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val content = findViewById<LinearLayout>(R.id.kc_details)
        UserManager.kcAccount.info.asJSON().forEach{ k, v ->
            if(v is Date) {
                content.addView(InfoLine(this, k.toString(), format.format(v)))
            }else{
                content.addView(InfoLine(this, k.toString(), v.toString()))
            }
        }
    }
}