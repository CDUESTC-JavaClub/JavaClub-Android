package club.cduestc

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import club.cduestc.net.NetManager


class LoginActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        val sharedPreference = getSharedPreferences("data", MODE_PRIVATE)
        val success = sharedPreference.getBoolean("base_last", false)
        val baseName = sharedPreference.getString("base_id", "").toString()
        val basePassword = sharedPreference.getString("base_password", "").toString()

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
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.setClass(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_login)
        val sharedPreference = getSharedPreferences("data", MODE_PRIVATE)
        val baseName = sharedPreference.getString("base_id", "")
        val basePassword = sharedPreference.getString("base_password", "")
        val success = sharedPreference.getBoolean("base_last", false)
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

        btn.setOnClickListener {
            it.isEnabled = false
            btn.text = "正在登录..."
            val editor = sharedPreference.edit()

            if(username.text.isEmpty() || password.text.isEmpty()) {
                Toast.makeText(this, "请正确填写用户名和密码！", Toast.LENGTH_SHORT).show()
                it.isEnabled = true
                btn.text = "登录"
                return@setOnClickListener
            }
            editor.putString("base_id", username.text.toString())
            editor.putString("base_password", password.text.toString())

            NetManager.createTask{
                if(!NetManager.login(username.text.toString(), password.text.toString())){
                    runOnUiThread {
                        it.isEnabled = true
                        (it as Button).text = "登录"
                        Toast.makeText(this, "登陆失败，可能是用户名或密码填写错误！", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    editor.putBoolean("base_last", true)
                    editor.apply()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}