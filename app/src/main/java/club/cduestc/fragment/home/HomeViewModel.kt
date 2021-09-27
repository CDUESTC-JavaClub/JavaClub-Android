package club.cduestc.fragment.home

import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    var url : String = ""
    var webView : WebViewClient? = null
    var chromeClient : WebChromeClient? = null

    fun init(url : String, webView : WebViewClient, chromeClient : WebChromeClient){
        this.url = url
        this.webView = webView
        this.chromeClient = chromeClient
    }
}