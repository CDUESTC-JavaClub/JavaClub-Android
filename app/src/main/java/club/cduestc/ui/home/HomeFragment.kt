package club.cduestc.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.webkit.WebChromeClient.FileChooserParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.databinding.FragmentHomeBinding
import club.cduestc.util.UserManager
import java.lang.RuntimeException

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
                binding.webTitle.text = web.title
            }
        }
        web.loadUrl(UserManager.index ?: "https://study.cduestc.club/index.php")

        binding.webBack.setOnClickListener {
            web.goBack()
        }

        return binding.root
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