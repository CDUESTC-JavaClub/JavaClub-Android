package club.cduestc.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.LoginActivity
import club.cduestc.R
import club.cduestc.databinding.FragmentSettingsBinding
import club.cduestc.util.NetManager
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

        binding.version.text = NetManager.getVersion()

        val sharedPreference = requireActivity().getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val auto = sharedPreference.getBoolean("settings_auto_dark", false)
        if (auto) {
            binding.switchDark.isEnabled = false
            binding.switchAuto.setDefOff(true)
        }
        if(sharedPreference.getBoolean("settings_dark_mode", false)) binding.switchDark.setDefOff(true)

        binding.switchDark.setOnClickListener {
            sharedPreference.edit().putBoolean("settings_dark_mode", binding.switchDark.animationCheck()).apply()
        }
        binding.switchAuto.setOnClickListener(this::switchAuto)
        binding.exit.setOnClickListener (this::exit)
        binding.update.setOnClickListener(this::update)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            val data = NetManager.update();
            if(data != null){
                val version = data.getString("data")
                if(NetManager.getVersion() != version){
                    requireActivity().runOnUiThread {
                        showDialog()
                    }
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
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setClass(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val view: View = LayoutInflater.from(context).inflate(R.layout.update_dialog, null)
        val btn = view.findViewById<Button>(R.id.btn_update)
        val dialog = builder.setView(view).create()
        btn.setOnClickListener {
            dialog.dismiss()
            val uri: Uri = Uri.parse("https://github.com/CDUESTC-JavaClub/JavaClub-Android/releases")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        dialog.show()
    }
}
