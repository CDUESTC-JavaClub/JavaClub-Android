package club.cduestc.ui.kc

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.databinding.FragmentKcBinding
import club.cduestc.util.KcManager
import club.cduestc.util.NetManager
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
        binding.saveKcBtn.setOnClickListener { savePassword(performance, binding.kcPassword.text.toString(), binding.kcId.text.toString()) }

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

    private fun savePassword(performance: SharedPreferences, pwd : String, id : String){
        postSave()
        NetManager.createTask{
            if(NetManager.bind(id, pwd)){
                performance.edit().putString("kc_password", pwd).apply()
                requireActivity().runOnUiThread { displayMenu(performance) }
                return@createTask
            }else{
                requireActivity().runOnUiThread {
                    endSave()
                    Toast.makeText(context, "登陆失败，账号或密码错误！", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun postSave(){
        binding.saveKcBtn.isEnabled = false
        binding.saveKcBtn.text = "正在验证..."
    }

    private fun endSave(){
        binding.saveKcBtn.isEnabled = true
        binding.saveKcBtn.text = "保存"
    }

    private fun displayMenu(performance: SharedPreferences){
        binding.tipBind.visibility = View.GONE
        binding.kcMenu.visibility = View.VISIBLE
        UserManager.kcPassword = performance.getString("kc_password", "")
    }
}