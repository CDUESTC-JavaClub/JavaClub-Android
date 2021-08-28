package club.cduestc.ui.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import club.cduestc.R
import club.cduestc.databinding.FragmentHomeBinding
import android.widget.RelativeLayout

import android.widget.ProgressBar
import android.widget.Toast
import club.cduestc.net.NetManager
import club.jw.auth.KcAccount
import android.webkit.WebResourceRequest

import android.os.Build

import android.annotation.TargetApi
import android.os.StrictMode


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val web = binding.forumWebView
        web.clearCache(true)
        web.visibility = View.GONE
        web.settings.javaScriptEnabled = true
        web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.webLoad.visibility = View.GONE
                web.visibility = View.VISIBLE
            }
        }
        NetManager.createTask{
            val index = NetManager.loginIndex()

            activity?.runOnUiThread{
                if(index == "error") web.loadUrl("https://www.bilibili.com/")
                else web.loadUrl(index)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}