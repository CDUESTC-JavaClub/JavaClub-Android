package club.cduestc.ui.bai.sub

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import club.cduestc.R
import club.cduestc.databinding.ActivityBaiInfoBinding
import club.cduestc.databinding.ActivityMainBinding
import club.cduestc.ui.bai.item.ScoreLine
import club.cduestc.util.AnimUtil
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager

class BaiInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bai_info)
        init()
    }

    private fun init(){
        NetManager.createTask{
            val list = UserManager.baiAccount.scoreAddList
            val score = UserManager.baiAccount.score
            this.runOnUiThread {
                list.forEach {
                    findViewById<LinearLayout>(R.id.score_add_list).addView(ScoreLine(this, it))
                }
                findViewById<TextView>(R.id.bai_score_jm).text = score.jm.toString()
                findViewById<TextView>(R.id.bai_score_bx).text = score.bx.toString()
                findViewById<TextView>(R.id.bai_score_dx).text = score.dx.toString()
                findViewById<TextView>(R.id.bai_score_md).text = score.md.toString()
                if(score.bx >= 10 && score.dx >= 10 && score.jm >= 30 && score.md >= 10){
                    findViewById<CardView>(R.id.finish_back).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                    findViewById<TextView>(R.id.finish_text).text = getString(R.string.bai_pass)
                }
                AnimUtil.hide(findViewById(R.id.loadActivity))
                findViewById<View>(R.id.score_add_menu).visibility = View.VISIBLE
            }
        }
    }
}