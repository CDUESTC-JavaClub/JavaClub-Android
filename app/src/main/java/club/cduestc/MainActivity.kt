package club.cduestc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.NavHostFragment
import club.cduestc.databinding.ActivityMainBinding
import club.cduestc.ui.bai.BaiFragment
import club.cduestc.ui.contest.ContestFragment
import club.cduestc.ui.home.HomeFragment
import club.cduestc.ui.kc.KcFragment
import club.cduestc.ui.nav.FixFragmentNavigator
import club.cduestc.ui.settings.SettingsFragment
import club.cduestc.util.AnimUtil
import club.cduestc.util.NetManager
import club.cduestc.util.UpdateUtil
import club.cduestc.util.UserManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        this.setDayNight()

        super.onCreate(savedInstanceState)
        window.navigationBarColor = Color.TRANSPARENT
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference = getSharedPreferences("data", MODE_PRIVATE)
        val baseName = sharedPreference.getString("base_id", "").toString()
        val basePassword = sharedPreference.getString("base_password", "").toString()
        binding.btnLogin.setOnClickListener{
            if(binding.policyCheck.isChecked){
                if(infoCheck()){
                    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                    saveLoginForm(sharedPreference)
                    binding.loginCard.toggle()
                    NetManager.createTask{ doLogin(sharedPreference, true) }
                }else{
                    Toast.makeText(this, getString(R.string.login_info_tip), Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, getString(R.string.login_check_tip), Toast.LENGTH_SHORT).show()
            }
        }
        binding.inputPwd.setText(basePassword)
        binding.inputId.setText(baseName)

        binding.linkPolicy.setOnClickListener { linkToWeb("https://study.cduestc.club/index.php?help/privacy-policy/") }
        binding.linkRegister.setOnClickListener { linkToWeb("https://study.cduestc.club/index.php?register/") }
        binding.linkForget.setOnClickListener { linkToWeb("https://study.cduestc.club/index.php?lost-password/") }
        binding.loginMask.setOnClickListener {  }
        AnimUtil.show(binding.loginMask)

        login(sharedPreference)
    }

    private fun infoCheck() : Boolean{
        return binding.inputId.text.isNotEmpty() && binding.inputPwd.text.isNotEmpty()
    }

    private fun setDayNight(){
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
    }

    private fun login(sharedPreference : SharedPreferences){
        val success = sharedPreference.getBoolean("base_last", false)
        NetManager.createTask{
            if(success){
                doLogin(sharedPreference, false)
            }else{
                runOnUiThread { binding.loginCard.toggle() }
            }
        }
    }

    private fun doLogin(sharedPreference : SharedPreferences, first : Boolean){
        val baseName = sharedPreference.getString("base_id", "").toString()
        val basePassword = sharedPreference.getString("base_password", "").toString()
        runOnUiThread { AnimUtil.show(binding.loginLoading, 0f, 1f) }
        if(!NetManager.login(baseName, basePassword)){
            val editor = sharedPreference.edit()
            editor.putBoolean("base_last", false)
            editor.apply()
            runOnUiThread {
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                AnimUtil.hide(binding.loginLoading)
                binding.loginCard.toggle()
            }
        }else{   //登陆完成
            sharedPreference
                .edit()
                .putBoolean("base_last", true)
                .apply()
            if(first || sharedPreference.getString("base_avatar_url", null) == null){
                this.firstLogin(sharedPreference)
            }
            initUserImage()
            runOnUiThread {
                AnimUtil.hide(binding.loginMask)
                AnimUtil.hide(binding.loginCardMain)
                AnimUtil.hide(binding.loginLoading)
                initView()
            }
        }
    }

    /**
     * 首次登陆
     */
    private fun firstLogin(sharedPreference: SharedPreferences){
        val data = NetManager.initForum()
        if(data != null){
            UserManager.index = data.getString("index")
            sharedPreference
                .edit()
                .putString("base_avatar_url", data.getString("urlAvatar"))
                .putString("base_background_url", data.getString("urlBackground"))
                .apply()
        }
    }

    /**
     * 保存登陆表单
     */
    private fun saveLoginForm(sharedPreference : SharedPreferences){
        sharedPreference
            .edit()
            .putString("base_id", binding.inputId.text.toString())
            .putString("base_password", binding.inputPwd.text.toString())
            .apply()
    }

    /**
     * 初始化用户图片（头像、背景）
     */
    private fun initUserImage(){
        NetManager.createTask{
            val s = getSharedPreferences("data", MODE_PRIVATE)
            UserManager.initImage(s.getString("base_avatar_url", null), s.getString("base_background_url", null))
        }
    }

    private fun initView(){
        //自定义导航栏复用Fragment处理
        val navView: BottomNavigationView = binding.navView
        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        val navController = NavHostFragment.findNavController(fragment!!)
        val fragmentNavigator = FixFragmentNavigator(this, fragment.childFragmentManager, fragment.id)
        val provider = navController.navigatorProvider
        provider.addNavigator(fragmentNavigator)
        val navGraph = initNavGraph(provider, fragmentNavigator)
        navController.graph = navGraph
        navView.setOnNavigationItemSelectedListener { item: MenuItem ->
            navController.navigate(item.itemId)
            true
        }
        checkUpdate()
    }

    private fun checkUpdate(){
        val sharedPreference = this.getSharedPreferences("settings", MODE_PRIVATE)
        val auto = sharedPreference.getBoolean("settings_auto_update", true)
        if (auto) {
            NetManager.createTask{
                val data = NetManager.update()
                if(data != null){
                    if(data.getInteger("status") != 200) return@createTask
                    val version = data.getJSONObject("data").getString("version")
                    if(UpdateUtil.getVersion() != version){
                        this.runOnUiThread {
                            UpdateUtil.showUpdateDialog(this, data.getJSONObject("data").getString("url"), version)
                        }
                        return@createTask
                    }
                }
            }
        }
    }

    /**
     * 返回时，直接结束
     */
    override fun onBackPressed() {
        finish()
    }

    /**
     * 自定义导航图
     */
    private fun initNavGraph(provider: NavigatorProvider, fragmentNavigator: FixFragmentNavigator): NavGraph {
        val navGraph = NavGraph(NavGraphNavigator(provider))

        val destination1 = fragmentNavigator.createDestination()
        destination1.id = R.id.navigation_home
        destination1.className = HomeFragment::class.java.canonicalName ?: ""
        destination1.label = resources.getString(R.string.title_home)
        navGraph.addDestination(destination1)

        val destination5 = fragmentNavigator.createDestination()
        destination5.id = R.id.navigation_contest
        destination5.className = ContestFragment::class.java.canonicalName ?: ""
        destination5.label = resources.getString(R.string.title_contest)
        navGraph.addDestination(destination5)

        val destination2 = fragmentNavigator.createDestination()
        destination2.id = R.id.navigation_kc
        destination2.className = KcFragment::class.java.canonicalName ?: ""
        destination2.label = resources.getString(R.string.title_kc)
        navGraph.addDestination(destination2)

        val destination3 = fragmentNavigator.createDestination()
        destination3.id = R.id.navigation_dashboard
        destination3.className = BaiFragment::class.java.canonicalName ?: ""
        destination3.label = resources.getString(R.string.title_bai)
        navGraph.addDestination(destination3)

        val destination4 = fragmentNavigator.createDestination()
        destination4.id = R.id.navigation_notifications
        destination4.className = SettingsFragment::class.java.canonicalName ?: ""
        destination4.label = resources.getString(R.string.title_settings)
        navGraph.addDestination(destination4)

        navGraph.startDestination = R.id.navigation_home
        return navGraph
    }

    private fun linkToWeb(url : String){
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}