package club.cduestc.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.MainActivity
import club.cduestc.R
import club.cduestc.databinding.FragmentSettingsBinding
import club.cduestc.util.AnimUtil
import club.cduestc.util.NetManager
import club.cduestc.util.UpdateUtil
import club.cduestc.util.UserManager


class SettingsFragment : Fragment() {

    private lateinit var notificationsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        loadImageResource()
        loadOauthInfo()

        binding.userName.text = UserManager.getUserName()
        binding.userSignature.text = UserManager.getSignature()
        binding.userId.text = UserManager.getBindId() ?: getString(R.string.settings_user_id_no)
        binding.userEmail.text = UserManager.getEmail()

        binding.version.text = UpdateUtil.getVersion()

        val sharedPreference = requireActivity().getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val auto = sharedPreference.getBoolean("settings_auto_dark", false)
        if (auto) {
            binding.switchDark.isEnabled = false
            binding.switchAuto.setDefOff(true)
        }
        val auto2 = sharedPreference.getBoolean("settings_auto_update", true)
        if (auto2) binding.switchUpdateAuto.setDefOff(true)

        if(sharedPreference.getBoolean("settings_dark_mode", false)) binding.switchDark.setDefOff(true)

        binding.switchDark.setOnClickListener {
            val dark = binding.switchDark.animationCheck()
            sharedPreference.edit().putBoolean("settings_dark_mode", dark).apply()
            AnimUtil.switchDayNight(dark)
        }
        binding.switchAuto.setOnClickListener(this::switchAuto)
        binding.switchUpdateAuto.setOnClickListener(this::switchAutoUpdate)
        binding.exit.setOnClickListener (this::exit)
        binding.update.setOnClickListener(this::update)

        binding.userGithubStatus.setOnClickListener {
            it as TextView
            if(it.text.equals(getString(R.string.settings_user_github_disconnected))) linkToWeb("https://study.cduestc.club/index.php?account/connected-accounts/")
        }
        binding.qqId.setOnClickListener {
            it as TextView
            if(it.text.equals(getString(R.string.settings_user_github_disconnected))) linkToWeb("https://study.cduestc.club/index.php?account/connected-accounts/")
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 第三方绑定信息
     */
    private fun loadOauthInfo(){
        NetManager.createTask{
            for (i in 1..10){
                if(UserManager.oauthInfo != null){
                    //QQ
                    if(UserManager.oauthInfo!!.containsKey("qq")){
                        requireActivity().runOnUiThread {
                            val data = UserManager.oauthInfo!!.getJSONObject("qq")
                            val nickname = data.getString("nickname")
                            val avatar = data.getString("figureurl_qq")
                            NetManager.createTask{
                                val bitmap = NetManager.getHttpBitmap(avatar)
                                activity?.runOnUiThread {
                                    AnimUtil.show(binding.qqAvatar, 0f, 1f)
                                    binding.qqAvatar.setImageBitmap(bitmap)
                                }
                            }
                            binding.qqId.text = nickname
                        }
                    }else{
                        binding.qqAvatar.layoutParams.width = 0
                    }
                    //Github
                    if(UserManager.oauthInfo!!.containsKey("github")){
                        requireActivity().runOnUiThread {
                            val data = UserManager.oauthInfo!!.getJSONObject("github")
                            NetManager.createTask{
                                val bitmap = NetManager.getHttpBitmap(data.getString("avatar_url"))
                                activity?.runOnUiThread {
                                    AnimUtil.show(binding.userGithubAvatar, 0f, 1f)
                                    binding.userGithubAvatar.setImageBitmap(bitmap)
                                }
                            }
                            binding.userGithubName.text = data.getString("login")
                            binding.userGithubRepo.text = data.getInteger("public_repos").toString()
                            binding.userGithubFans.text = data.getInteger("followers").toString()

                            binding.userGithubHome.text = Html.fromHtml(getString(R.string.settings_user_github_home, data.getString("html_url")))
                            binding.userGithubHome.setOnClickListener { linkToWeb(data.getString("html_url")) }
                            binding.userGithubJoin.text = getString(R.string.settings_user_github_join, data.getString("created_at").substring(0..9))

                            binding.card0.toggle()
                            binding.userGithubStatus.text = getString(R.string.settings_user_github_connected)
                        }
                    }
                    break
                }
                Thread.sleep(1000)
            }
        }
    }

    private fun loadImageResource(){
        NetManager.createTask{
            for (i in 1..20){
                if(UserManager.getBackground() != null) {
                    activity?.runOnUiThread {
                        AnimUtil.show(binding.userBackground, 0f, 1f)
                        binding.userBackground.setImageBitmap(UserManager.getBackground())
                    }
                    break
                }
                Thread.sleep(1000)
            }
        }
        NetManager.createTask{
            for (i in 1..20){
                if(UserManager.getHeader() != null){
                    activity?.runOnUiThread {
                        AnimUtil.show(binding.userHeader, 0f, 1f)
                        binding.userHeader.setImageBitmap(UserManager.getHeader())
                    }
                    break
                }
                Thread.sleep(1000)
            }
        }
    }

    private fun switchAutoUpdate(view: View){
        val sharedPreference = requireActivity().getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        if(binding.switchUpdateAuto.animationCheck()){
            sharedPreference.edit().putBoolean("settings_auto_update", true).apply()
        }else{
            sharedPreference.edit().putBoolean("settings_auto_update", false).apply()
        }
    }

    private fun switchAuto(view: View){
        val sharedPreference = requireActivity().getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        if(binding.switchAuto.animationCheck()){
            binding.switchDark.isEnabled = false
            sharedPreference.edit().putBoolean("settings_auto_dark", true).apply()
        }else{
            binding.switchDark.isEnabled = true
            sharedPreference.edit().putBoolean("settings_auto_dark", false).apply()
        }
    }

    private fun update(view: View){
        view.isEnabled = false
        (view as Button).text = getString(R.string.settings_btn_update_do)
        NetManager.createTask{
            val data = NetManager.update()
            if(data != null){
                if(data.getInteger("status") != 200) return@createTask
                val version = data.getJSONObject("data").getString("version")
                if(UpdateUtil.getVersion() != version){
                    requireActivity().runOnUiThread {
                        view.isEnabled = true
                        view.text = getString(R.string.settings_btn_update)
                        UpdateUtil.showUpdateDialog(requireContext(), data.getJSONObject("data").getString("url"), version)
                    }
                    return@createTask
                }
                requireActivity().runOnUiThread {
                    view.isEnabled = true
                    view.text = getString(R.string.settings_btn_update)
                    Toast.makeText(context, getString(R.string.settings_btn_update_no), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun exit(view: View){
        val share = requireActivity().getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE)
        share
            .edit()
            .remove("saved_cookie")
            .remove("base_last")
            .remove("base_id")
            .remove("base_password")
            .remove("base_avatar_url")
            .remove("base_background_url")
            .apply()
        UserManager.oauthInfo = null
        NetManager.createTask(NetManager::logout)
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setClass(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    private fun linkToWeb(url : String){
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}
