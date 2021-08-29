package club.cduestc.ui.kc

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.databinding.FragmentKcBinding
import club.cduestc.util.UserManager

class KcFragment : Fragment() {

    private lateinit var kcViewModel: KcViewModel
    private var _binding: FragmentKcBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        kcViewModel = ViewModelProvider(this).get(KcViewModel::class.java)
        _binding = FragmentKcBinding.inflate(inflater, container, false)

        val performance = requireActivity().getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE)
        if(UserManager.getBindId() != null && performance.contains("kc_password")) displayMenu(performance)
        if(UserManager.getBindId() != null) initEdit()
        binding.saveKcBtn.setOnClickListener { savePassword(performance, binding.kcPassword.text.toString()) }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initEdit(){
        binding.kcId.setText(UserManager.getBindId())
        binding.kcId.isEnabled = false
    }

    private fun savePassword(performance: SharedPreferences, pwd : String){
        performance.edit().putString("kc_password", pwd).apply()
        TODO("登陆验证是否成功")
    }

    private fun displayMenu(performance: SharedPreferences){
        binding.tipBind.visibility = View.GONE
        binding.kcMenu.visibility = View.VISIBLE
        UserManager.kcPassword = performance.getString("kc_password", "")
    }
}