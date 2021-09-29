package club.cduestc.ui.kc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.R
import club.cduestc.databinding.FragmentKcBinding
import club.cduestc.ui.kc.sub.KcScoreActivity
import club.cduestc.ui.kc.sub.KcStudentActivity
import club.cduestc.ui.kc.sub.KcTableActivity
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager
import club.jw.auth.KcAccount
import org.apache.commons.io.IOUtils
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*
import java.util.concurrent.CountDownLatch


class KcFragment : Fragment() {

    private lateinit var kcViewModel: KcViewModel
    private var _binding: FragmentKcBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        kcViewModel = ViewModelProvider(this).get(KcViewModel::class.java)
        _binding = FragmentKcBinding.inflate(inflater, container, false)
        val performance = requireActivity().getSharedPreferences("data", AppCompatActivity.MODE_PRIVATE)

        club.jw.net.NetManager.setCaptcha {
            val base64 = toBase64(it)
            val ocrResult = NetManager.ocr(base64)
            if(ocrResult == "failed"){
                var str = ""
                if(binding.kcMenu.visibility == View.GONE){  //是否为首次登陆操作（重登陆会自动完成登陆操作）
                    val latch = CountDownLatch(1)
                    requireActivity().runOnUiThread {
                        binding.tipCaptcha.visibility = View.VISIBLE
                        binding.kcLoading.visibility = View.GONE
                        binding.kcCaptchaInput.setText("")
                        binding.kcCaptchaImage.setImageBitmap(BitmapFactory.decodeStream(toStream(base64)))
                        binding.kcCaptchaInput.requestFocus()
                        binding.kcCaptchaImage.visibility = View.VISIBLE
                        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(binding.kcCaptchaInput, InputMethodManager.SHOW_IMPLICIT)
                        binding.btnKcCaptcha.setOnClickListener {
                            str = binding.kcCaptchaInput.text.toString()
                            if(str.isEmpty()) {
                                Toast.makeText(context, getString(R.string.kc_fragment_tip_blank), Toast.LENGTH_SHORT).show()
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
                }
                it.close()
                str
            }else{
                ocrResult
            }
        }

        if(UserManager.getBindId() != null && performance.contains("kc_password")) displayMenu(performance, false, 0)
        if(UserManager.getBindId() != null) initEdit(performance)
        binding.saveKcBtn.setOnClickListener { savePassword(performance, binding.kcPassword.text.toString(), binding.kcId.text.toString()) }

        binding.btnKcScore.setOnClickListener(this::displayScore)
        binding.btnKcClass.setOnClickListener(this::displayClass)
        binding.btnKcUser.setOnClickListener(this::displayInfo)

        return binding.root
    }

    private fun initInfoCard(){
        binding.kcInfoName.text = UserManager.kcAccount.name
        binding.kcInfoDepartment.text = getString(R.string.kc_fragment_card_line_1, UserManager.kcAccount.info.get("年级"), UserManager.kcAccount.info.get("专业"))
        binding.kcInfoId.text = getString(R.string.kc_fragment_card_line_2, UserManager.kcAccount.id)
        binding.kcInfoEmail.text = getString(R.string.kc_fragment_card_line_3, UserManager.kcAccount.info.get("院系"), UserManager.kcAccount.info.get("学历层次"))
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
        NetManager.createTask{
            if(NetManager.bind(id, pwd)){
                performance.edit().putString("kc_password", pwd).apply()
                UserManager.setBindId(id)
                requireActivity().runOnUiThread { displayMenu(performance, false, 0) }
                return@createTask
            }else{
                requireActivity().runOnUiThread {
                    endSave()
                    Toast.makeText(context, getString(R.string.kc_fragment_tip_account_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun postSave(){
        binding.saveKcBtn.isEnabled = false
        binding.saveKcBtn.text = getString(R.string.kc_fragment_bind_btn_login_do)
    }

    private fun endSave(){
        binding.saveKcBtn.isEnabled = true
        binding.saveKcBtn.text = getString(R.string.kc_fragment_bind_btn_login)
    }

    private fun displayMenu(performance: SharedPreferences, first : Boolean, limit : Int ){
        binding.tipBind.visibility = View.GONE
        binding.kcLoading.visibility = View.VISIBLE
        val pwd = performance.getString("kc_password", "")
        NetManager.createTask{
            try {
                UserManager.kcAccount = KcAccount.create(UserManager.getBindId(), pwd)
                UserManager.kcAccount.login()
                requireActivity().runOnUiThread {
                    binding.kcMenu.visibility = View.VISIBLE
                    binding.kcLoading.visibility = View.GONE
                    initInfoCard()
                }
            }catch (e : Exception){
                if(e.message == "验证码错误！"){
                    if(limit < 10) {
                        Log.i("OCR", "识别失败，正在重新请求...")
                        displayMenu(performance, first, limit + 1)
                        return@createTask
                    }
                }
                requireActivity().runOnUiThread {
                    binding.kcLoading.visibility = View.GONE
                    binding.tipBind.visibility = View.VISIBLE
                    endSave()
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun toBase64(inputStream: InputStream): String {
        val bytes: ByteArray = IOUtils.toByteArray(inputStream)
        return Base64.getEncoder().encodeToString(bytes)
    }

    private fun toStream(base64string: String): InputStream? {
        var stream: ByteArrayInputStream? = null
        try {
            val decoder = Base64.getDecoder()
            val bytes1: ByteArray = decoder.decode(base64string)
            stream = ByteArrayInputStream(bytes1)
        } catch (e: Exception) {
            // TODO: handle exception
        }
        return stream
    }
}