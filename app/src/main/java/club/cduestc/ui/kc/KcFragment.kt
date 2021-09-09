package club.cduestc.ui.kc

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.MainActivity
import club.cduestc.databinding.FragmentKcBinding
import club.cduestc.ui.kc.sub.KcScoreActivity
import club.cduestc.ui.kc.sub.KcStudentActivity
import club.cduestc.ui.kc.sub.KcTableActivity
import club.cduestc.util.KcManager
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager
import club.jw.auth.KcAccount
import java.lang.Exception
import java.util.concurrent.CountDownLatch

class KcFragment : Fragment() {

    private lateinit var kcViewModel: KcViewModel
    private var _binding: FragmentKcBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        kcViewModel = ViewModelProvider(this).get(KcViewModel::class.java)
        _binding = FragmentKcBinding.inflate(inflater, container, false)

        val performance = requireActivity().getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE)
        if(UserManager.getBindId() != null && performance.contains("kc_password")) displayMenu(performance, false)
        if(UserManager.getBindId() != null) initEdit(performance)
        binding.saveKcBtn.setOnClickListener { savePassword(performance, binding.kcPassword.text.toString(), binding.kcId.text.toString()) }

        binding.btnKcScore.setOnClickListener(this::displayScore)
        binding.btnKcClass.setOnClickListener(this::displayClass)
        binding.btnKcUser.setOnClickListener(this::displayInfo)

        return binding.root
    }

    private fun initInfoCard(){
        binding.kcInfoName.text = UserManager.kcAccount.name
        binding.kcInfoDepartment.text = "${UserManager.kcAccount.info.get("年级")}级 ${UserManager.kcAccount.info.get("专业")}"
        binding.kcInfoId.text = "学号：${UserManager.kcAccount.id}"
        binding.kcInfoEmail.text = "所属：${UserManager.kcAccount.info.get("院系")} (${UserManager.kcAccount.info.get("学历层次")})"
    }

    private fun displayInfo(it : View){
        val intent = Intent(context, KcStudentActivity::class.java)
        startActivity(intent)
    }

    private fun displayClass(it : View){
        val intent = Intent(context, KcTableActivity::class.java)
        startActivity(intent)
    }

    private fun displayScore(it : View){
        val intent = Intent(context, KcScoreActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initEdit(performance: SharedPreferences){
        binding.kcPassword.setText(performance.getString("kc_password", ""))
        binding.kcId.setText(UserManager.getBindId())
        binding.kcId.isEnabled = false
    }

    private fun savePassword(performance: SharedPreferences, pwd : String, id : String){
        postSave()
        performance.edit().putString("kc_password", pwd).apply()
        UserManager.setBindId(id)
        displayMenu(performance, true, id, pwd)
    }

    private fun postSave(){
        binding.saveKcBtn.isEnabled = false
        binding.saveKcBtn.text = "正在验证..."
    }

    private fun endSave(){
        binding.saveKcBtn.isEnabled = true
        binding.saveKcBtn.text = "保存"
    }

    private fun displayMenu(performance: SharedPreferences, first : Boolean, vararg args: String){
        binding.tipBind.visibility = View.GONE
        binding.tipCaptcha.visibility = View.VISIBLE
        binding.kcCaptchaImage.visibility = View.GONE
        val pwd = performance.getString("kc_password", "")
        NetManager.createTask{
            try {
                club.jw.net.NetManager.setCaptcha {
                    val latch = CountDownLatch(1)
                    var str = ""
                    requireActivity().runOnUiThread {
                        binding.kcCaptchaInput.setText("")
                        binding.kcCaptchaImage.setImageBitmap(BitmapFactory.decodeStream(it))
                        binding.kcCaptchaInput.requestFocus()
                        binding.kcCaptchaImage.visibility = View.VISIBLE
                        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(binding.kcCaptchaInput, InputMethodManager.SHOW_IMPLICIT)
                        it.close()
                        binding.btnKcCaptcha.setOnClickListener {
                            str = binding.kcCaptchaInput.text.toString()
                            if(str.isEmpty()) {
                                Toast.makeText(context, "请输入验证码!", Toast.LENGTH_SHORT).show()
                            }else{
                                val v: View = requireActivity().window.peekDecorView()
                                imm.hideSoftInputFromWindow(v.windowToken, 0)
                                binding.kcLoading.visibility = View.VISIBLE
                                binding.tipCaptcha.visibility = View.GONE
                                latch.countDown()
                            }
                        }
                    }
                    latch.await()
                    str
                }
                UserManager.kcAccount = KcAccount.create(UserManager.getBindId(), pwd)
                UserManager.kcAccount.login()
                if(first) NetManager.bind(args[0], args[1])
                requireActivity().runOnUiThread {
                    binding.kcMenu.visibility = View.VISIBLE
                    binding.kcLoading.visibility = View.GONE
                    initInfoCard()
                }
            }catch (e : Exception){
                e.printStackTrace()
                requireActivity().runOnUiThread {
                    binding.kcMenu.visibility = View.GONE
                    binding.kcLoading.visibility = View.GONE
                    binding.tipBind.visibility = View.VISIBLE
                    endSave()
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}