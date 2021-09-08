package club.cduestc.ui.bai.sub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import club.byjh.net.WebManager
import club.byjh.net.enums.StatusType
import club.cduestc.R
import club.cduestc.ui.bai.item.ActivityLine
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager

class BaiListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bai_list)

        NetManager.createTask{
            WebManager.getAllActivities(StatusType.ALL).stream().limit(50).forEach {
                runOnUiThread{
                    findViewById<LinearLayout>(R.id.bai_activity_list).addView(ActivityLine(this, this, it))
                }
            }
        }
    }
}