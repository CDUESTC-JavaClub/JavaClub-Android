package club.cduestc.ui.bai.sub

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import club.byjh.entity.activity.SignedActivity
import club.cduestc.R
import club.cduestc.ui.bai.item.MyActivityLine
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager
import org.w3c.dom.Text

class BaiMyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bai_my)

        init()
    }

    private fun init(){
        val list1 = findViewById<LinearLayout>(R.id.myActivityList1)
        val list2 = findViewById<LinearLayout>(R.id.myActivityList2)
        var signed = 0
        var wait = 0
        NetManager.createTask{
            val acts = UserManager.baiAccount.activities
            this.runOnUiThread {
                acts.forEach {
                    if(it.stat.equals("已参加")){
                        signed++
                        list2.addView(MyActivityLine(this, this, it, this::showCode))
                    } else {
                        wait++
                        list1.addView(MyActivityLine(this, this, it, this::showCode))
                    }
                }
                findViewById<TextView>(R.id.count_signed).text = signed.toString()
                findViewById<TextView>(R.id.count_wait).text = wait.toString()
                findViewById<View>(R.id.loadActivity).visibility = View.GONE
                findViewById<View>(R.id.my_activity_menu).visibility = View.VISIBLE
            }
        }
    }

    private fun showCode(v : View){
        val p = v.parent.parent.parent.parent
        p as MyActivityLine
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.Translucent_NoTitle)
        val view: View = LayoutInflater.from(this).inflate(R.layout.bai_activity_my_qrcode, null)
        val dialog = builder.setView(view).create()
        NetManager.createTask{
            val map = UserManager.getHttpBitmap(p.activity.checkCode)
            this.runOnUiThread {
                view.findViewById<TextView>(R.id.qrcode_text).text = "核验码："+p.activity.qrCode
                view.findViewById<ImageView>(R.id.qrcode_img_src).setImageBitmap(map)
                dialog.show()
            }
        }
    }
}