package club.cduestc.ui.bai

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.byjh.entity.account.BaiAccount
import club.byjh.exception.ByjhAssistantException
import club.byjh.net.WebManager
import club.byjh.net.enums.StatusType
import club.cduestc.databinding.FragmentBaiBinding
import club.cduestc.ui.bai.sub.BaiListActivity
import club.cduestc.ui.kc.sub.KcStudentActivity
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager

class BaiFragment : Fragment() {

    private lateinit var baiViewModel: BaiViewModel
    private var _binding: FragmentBaiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        baiViewModel = ViewModelProvider(this).get(BaiViewModel::class.java)
        _binding = FragmentBaiBinding.inflate(inflater, container, false)

        main()
        val performance = requireActivity().getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE)
        binding.saveKcBtn.setOnClickListener { savePassword(performance, binding.kcPassword.text.toString(), binding.kcId.text.toString()) }

        //binding.btnBaiAll.setOnClickListener(this::listAllActivity)

        return binding.root
    }

    private fun listAllActivity(v : View){
        val intent = Intent(context, BaiListActivity::class.java)
        startActivity(intent)
    }

    private fun main(){
        NetManager.createTask{
            if(NetManager.isBaiNetwork()){
                requireActivity().runOnUiThread{
                    binding.baiTipBind.visibility = View.VISIBLE
                    val performance = requireActivity().getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE)
                    if(UserManager.getBindId() != null && performance.contains("bai_password")) displayMenu(performance)
                    if(UserManager.getBindId() != null) initEdit(performance)
                }
            }else{
                requireActivity().runOnUiThread{
                    binding.baiTipBind.visibility = View.GONE
                    binding.baiNetErr.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initEdit(performance: SharedPreferences){
        if(UserManager.getBindId() == null || UserManager.getBindId()!!.isEmpty()){
            binding.saveKcBtn.text = "请先绑定教务系统账号"
            binding.saveKcBtn.isEnabled = false
            return
        }
        binding.kcPassword.setText(performance.getString("bai_password", ""))
        binding.kcId.setText(UserManager.getBindId())
        binding.kcId.isEnabled = false
    }

    private fun savePassword(performance: SharedPreferences, pwd : String, id : String){
        postSave()
        NetManager.createTask{
            try {
                val account = BaiAccount.createAccount(id, pwd)
                account.login()
                performance.edit().putString("bai_password", pwd).apply()
                requireActivity().runOnUiThread { displayMenu(performance) }
                return@createTask
            }catch (e : Exception){
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

    private fun displayMenu(performance : SharedPreferences){
        binding.baiTipBind.visibility = View.GONE
        binding.baiLoading.visibility = View.VISIBLE
        NetManager.createTask{
            try {
                UserManager.baiAccount = BaiAccount.createAccount(UserManager.getBindId(), performance.getString("bai_password", ""))
                UserManager.baiAccount.login()
                val score = UserManager.baiAccount.score
                val header = UserManager.getHttpBitmap(UserManager.baiAccount.headImgUrl)
                requireActivity().runOnUiThread {
                    binding.baiLoading.visibility = View.GONE
                    binding.baiMenu.visibility = View.VISIBLE

                    binding.baiScoreBx.text = score.bx.toString()
                    binding.baiScoreJm.text = score.jm.toString()
                    binding.baiScoreDx.text = score.dx.toString()
                    binding.baiScoreMd.text = score.md.toString()
                    binding.baiScoreAll.text = score.allScore.toString()
                    binding.baiScoreAllStatus.text = if(score.allScore >= 60) "已达标" else "继续努力"

                    binding.baiUserHead.setImageBitmap(header)
                    binding.baiUserName.text = UserManager.baiAccount.userName
                    binding.baiUserInfo.text = "${UserManager.baiAccount.major} ${UserManager.baiAccount.clazz}"
                    binding.baiUserIndent.text = UserManager.baiAccount.identity
                }
            }catch (e : ByjhAssistantException){
                requireActivity().runOnUiThread {
                    binding.baiMenu.visibility = View.GONE
                    binding.baiLoading.visibility = View.GONE
                    binding.baiTipBind.visibility = View.VISIBLE
                }
                Toast.makeText(context, "登陆失败，请检查你的学号和密码！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}