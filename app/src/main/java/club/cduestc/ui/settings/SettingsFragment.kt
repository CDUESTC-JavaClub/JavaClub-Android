package club.cduestc.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.databinding.FragmentSettingsBinding
import club.cduestc.net.UserManager

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
        binding.switchAuto.setOnClickListener {
            if(binding.switchAuto.animationCheck()){
                binding.switchDark.isEnabled = false
                sharedPreference.edit().putBoolean("settings_auto_dark", true).apply()
            }else{
                binding.switchDark.isEnabled = true
                sharedPreference.edit().putBoolean("settings_auto_dark", false).apply()
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
