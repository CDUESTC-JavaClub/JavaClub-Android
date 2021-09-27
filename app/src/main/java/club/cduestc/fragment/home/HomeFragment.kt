package club.cduestc.fragment.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
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

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding : FragmentHomeBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val web = binding.forumWebView
        if(savedInstanceState != null){
            web.restoreState(savedInstanceState)
        } else{
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
            web.loadUrl("https://www.bilibili.com/")
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.forumWebView.saveState(outState)
    }

    var mFilePathCallback : ValueCallback<Array<Uri>>? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            val result: Uri? = if (resultCode != Activity.RESULT_OK) null else data?.data
            if (result != null) {
                val resultsArray = arrayOf(result)
                mFilePathCallback!!.onReceiveValue(resultsArray)
            } else mFilePathCallback!!.onReceiveValue(null)
        }
    }

    private fun chromeClient() : WebChromeClient {
        return object : WebChromeClient(){
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                mFilePathCallback = filePathCallback
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                startActivityForResult(intent, 1)
                return true
            }
        }
    }

    private fun webView() : WebViewClient{
        return object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if(url.contains("https://qm.qq.com/")) {
                    toWeb()
                    binding.forumWebView.goBack()
                }
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.webLoad.visibility = View.GONE
                binding.forumWebView.visibility = View.VISIBLE
            }
        }
    }

    private fun toWeb(){
        val uri: Uri = Uri.parse("https://jq.qq.com/?_wv=1027&k=338Zkbu3")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}