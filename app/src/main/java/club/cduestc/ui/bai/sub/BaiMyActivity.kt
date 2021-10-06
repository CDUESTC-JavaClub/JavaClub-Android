package club.cduestc.ui.bai.sub

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import club.byjh.exception.ActivityOprException
import club.cduestc.R
import club.cduestc.ui.bai.item.MyActivityLine
import club.cduestc.util.AnimUtil
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager

class BaiMyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bai_my)

        init()
    }

    private fun init(){
        val list1 = findViewById<LinearLayout>(R.id.myActivityList1)
        val list2 = findViewById<LinearLayout>(R.id.myActivityList2)
        val list3 = findViewById<LinearLayout>(R.id.myActivityList3)
        list1.removeAllViews()
        list2.removeAllViews()
        list3.removeAllViews()
        var signed = 0
        var wait = 0
        NetManager.createTask{
            val acts = UserManager.baiAccount.activities
            this.runOnUiThread {
                acts.forEach {
                    when {
                        it.stat.equals("已参加") -> {
                            signed++
                            list2.addView(MyActivityLine(this, this, it, this::showCode))
                        }
                        it.stat.equals("缺席") -> {
                            list3.addView(MyActivityLine(this, this, it, this::showCode))
                        }
                        else -> {
                            wait++
                            list1.addView(MyActivityLine(this, this, it, this::showCode))
                        }
                    }
                }
                findViewById<TextView>(R.id.count_signed).text = signed.toString()
                findViewById<TextView>(R.id.count_wait).text = wait.toString()
                AnimUtil.hide(findViewById<View>(R.id.loadActivity))
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
            val map = NetManager.getHttpBitmap(p.activity.checkCode)
            this.runOnUiThread {
                if(p.activity.stat.equals("已报名")) {
                    val btn = view.findViewById<Button>(R.id.cancel_activity)
                    btn.setTextColor(Color.parseColor("#F44336"))
                    btn.isEnabled = true
                    btn.setOnClickListener {
                        NetManager.createTask{
                            try{
                                UserManager.baiAccount.cancelActivity(p.activity.id)
                                dialog.dismiss()
                                this.runOnUiThread{
                                    findViewById<View>(R.id.loadActivity).visibility = View.VISIBLE
                                    findViewById<View>(R.id.my_activity_menu).visibility = View.GONE
                                    Toast.makeText(this, getString(R.string.bai_activity_cancel_success), Toast.LENGTH_LONG).show()
                                    init()
                                }
                            }catch (e : ActivityOprException){
                                this.runOnUiThread{ Toast.makeText(this, e.message, Toast.LENGTH_LONG).show() }
                            }
                        }
                    }
                }
                view.findViewById<TextView>(R.id.qrcode_text).text = getString(R.string.bai_my_qrcode, p.activity.qrCode)
                view.findViewById<ImageView>(R.id.qrcode_img_src).setImageBitmap(map)
                dialog.show()
            }
        }
    }
}