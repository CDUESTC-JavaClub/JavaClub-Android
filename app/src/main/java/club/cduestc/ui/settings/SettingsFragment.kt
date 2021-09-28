package club.cduestc.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.LoginActivity
import club.cduestc.MainActivity
import club.cduestc.R
import club.cduestc.databinding.FragmentSettingsBinding
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

        binding.userBackground.setImageBitmap(UserManager.getBackground())
        binding.userHeader.setImageBitmap(UserManager.getHeader())
        binding.userName.text = UserManager.getUserName()
        binding.userSignature.text = UserManager.getSignature()
        binding.userId.text = UserManager.getBindId() ?: "未绑定"
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
            sharedPreference.edit().putBoolean("settings_dark_mode", binding.switchDark.animationCheck()).apply()
        }
        binding.switchAuto.setOnClickListener(this::switchAuto)
        binding.switchUpdateAuto.setOnClickListener(this::switchAutoUpdate)
        binding.exit.setOnClickListener (this::exit)
        binding.update.setOnClickListener(this::update)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        (view as Button).text = "正在检查更新..."
        NetManager.createTask{
            val data = NetManager.update()
            if(data != null){
                if(data.getInteger("status") != 200) return@createTask
                val version = data.getJSONObject("data").getString("version")
                if(UpdateUtil.getVersion() != version){
                    requireActivity().runOnUiThread {
                        view.isEnabled = true
                        view.text = "检查更新"
                        UpdateUtil.showUpdateDialog(requireContext(), data.getJSONObject("data").getString("url"), version)
                    }
                    return@createTask
                }
                requireActivity().runOnUiThread {
                    view.isEnabled = true
                    view.text = "检查更新"
                    Toast.makeText(context, "已经是最新版本啦！", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun exit(view: View){
        val share = requireActivity().getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE)
        share
            .edit()
            .remove("base_last")
            .remove("base_id")
            .remove("base_password")
            .apply()
        NetManager.createTask(NetManager::logout)
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setClass(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }
}
