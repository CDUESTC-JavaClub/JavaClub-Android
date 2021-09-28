package club.cduestc.ui.home

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.databinding.FragmentHomeBinding
import club.cduestc.util.UserManager


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val web = binding.forumWebView
        if(UserManager.index != null) web.clearCache(true)
        web.visibility = View.GONE
        web.settings.javaScriptEnabled = true
        web.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if(url.contains("https://qm.qq.com/")) {
                    toWeb()
                    web.goBack()
                }
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.webLoad.visibility = View.GONE
                web.visibility = View.VISIBLE
            }
        }
        web.webChromeClient = object : WebChromeClient(){
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                mFilePathCallback = filePathCallback
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, 1)
                return true
            }
        }
        web.setOnKeyListener { _, i, keyEvent ->
            if(i == KeyEvent.KEYCODE_BACK){
                if(web.canGoBack()){
                    if(keyEvent.action == KeyEvent.ACTION_DOWN) web.goBack()
                }else{
                    if(keyEvent.action == KeyEvent.ACTION_DOWN) requireActivity().onBackPressed()
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        web.loadUrl(UserManager.index ?: "https://study.cduestc.club/index.php")
        return binding.root
    }

    var mFilePathCallback : ValueCallback<Array<Uri>>? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            val result: Uri? = if (resultCode != RESULT_OK) null else data?.data
            if (result != null) {
                val resultsArray = arrayOf(result)
                mFilePathCallback!!.onReceiveValue(resultsArray)
            } else mFilePathCallback!!.onReceiveValue(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toWeb(){
        val uri: Uri = Uri.parse("https://jq.qq.com/?_wv=1027&k=338Zkbu3")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}