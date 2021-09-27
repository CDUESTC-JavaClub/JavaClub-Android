package club.cduestc

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import club.cduestc.util.NetManager
import android.content.IntentFilter
import club.cduestc.ui.kc.widget.KcClassWidget
import club.cduestc.util.UserManager
import com.alibaba.fastjson.JSONObject


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        val settingsPreference = getSharedPreferences("settings", MODE_PRIVATE)
        if(!settingsPreference.contains("settings_auto_dark")) settingsPreference.edit().putBoolean("settings_auto_dark", true).apply()
        val auto = settingsPreference.getBoolean("settings_auto_dark", false)
        val dark = settingsPreference.getBoolean("settings_dark_mode", false)
        if(!auto){
            if (dark){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        super.onCreate(savedInstanceState)
        window.navigationBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_login)

        val sharedPreference = getSharedPreferences("data", MODE_PRIVATE)
        val success = sharedPreference.getBoolean("base_last", false)
        val baseName = sharedPreference.getString("base_id", "").toString()
        val basePassword = sharedPreference.getString("base_password", "").toString()

        this.autoLogin(success, baseName, basePassword, sharedPreference)  //第二次自动登陆

        val btn = findViewById<Button>(R.id.btn_login)
        if(success) {
            btn.isEnabled = false
            btn.text = "正在登录..."
        }else{
            val mask = findViewById<View>(R.id.login_mask)
            mask.visibility = View.GONE
        }

        val username = findViewById<EditText>(R.id.input_id)
        val password = findViewById<EditText>(R.id.input_pwd)
        username.setText(baseName)
        password.setText(basePassword)

        btn.setOnClickListener(this::btnLogin)
        findViewById<TextView>(R.id.link_forget).setOnClickListener(this::linkForget)
        findViewById<TextView>(R.id.link_register).setOnClickListener(this::linkRegister)
    }

    /**
     * 初始化用户图片（头像、背景）
     */
    private fun initUserImage(){
        val s = getSharedPreferences("data", MODE_PRIVATE)
        UserManager.initImage(s.getString("base_avatar_url", null), s.getString("base_background_url", null))
    }

    private fun autoLogin(success : Boolean, baseName : String, basePassword : String, sharedPreference : SharedPreferences){
        NetManager.createTask{
            if(success){
                if(!NetManager.login(baseName, basePassword)){
                    val editor = sharedPreference.edit()
                    editor.putBoolean("base_last", false)
                    editor.apply()
                    runOnUiThread {
                        val btn = findViewById<Button>(R.id.btn_login)
                        btn.isEnabled = true
                        btn.text = "登录"
                        val mask = findViewById<View>(R.id.login_mask)
                        mask.visibility = View.GONE
                    }
                }else{
                    initUserImage()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.setClass(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun btnLogin(view: View){
        val sharedPreference = getSharedPreferences("data", MODE_PRIVATE)
        val username = findViewById<EditText>(R.id.input_id)
        val password = findViewById<EditText>(R.id.input_pwd)
        val it = view as Button
        it.isEnabled = false
        it.text = "正在登录..."
        val editor = sharedPreference.edit()

        if(username.text.isEmpty() || password.text.isEmpty()) {
            Toast.makeText(this, "请正确填写用户名和密码！", Toast.LENGTH_SHORT).show()
            it.isEnabled = true
            it.text = "登录"
            return
        }
        editor.putString("base_id", username.text.toString())
        editor.putString("base_password", password.text.toString())

        NetManager.createTask{
            if(NetManager.login(username.text.toString(), password.text.toString())){
                val data = NetManager.initForum()
                if(data != null){
                    UserManager.index = data.getString("index")
                    editor.putString("base_avatar_url", data.getString("urlAvatar"))
                    editor.putString("base_background_url", data.getString("urlBackground"))
                    editor.putBoolean("base_last", true)
                    editor.apply()
                    initUserImage()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.setClass(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    return@createTask
                }
            }
            runOnUiThread {
                it.isEnabled = true
                it.text = "登录"
                Toast.makeText(this, "登陆失败，可能是用户名或密码填写错误！", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun linkForget(view: View){
        val uri: Uri = Uri.parse("https://study.cduestc.club/index.php?lost-password/")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun linkRegister(view: View){
        val uri: Uri = Uri.parse("https://study.cduestc.club/index.php?register/")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}