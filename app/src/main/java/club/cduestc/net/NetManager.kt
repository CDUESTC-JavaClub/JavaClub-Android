package club.cduestc.net

import android.R.attr
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity
import club.cduestc.MainActivity
import com.alibaba.fastjson.JSONObject
import org.apache.commons.codec.binary.Base64
import org.jsoup.Jsoup
import java.io.ByteArrayOutputStream
import java.lang.IllegalStateException
import java.lang.RuntimeException
import java.net.HttpCookie
import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import java.util.concurrent.Executors
import javax.crypto.Cipher
import kotlin.collections.ArrayList


object NetManager {

    private var ip = "http://192.168.10.3:8080/api"
    private var executorService = Executors.newFixedThreadPool(10)

    fun createTask(task : Runnable){
        executorService.execute(task)
    }

    fun loginIndex() : String{
        val key = get("/auth/public-key").getString("data");
        val req = mapOf("id" to "Test", "password" to encrypt("123456", key), "remember-me" to "true")
        val res = post("/auth/login", req)
        if(res.getIntValue("status") != 200) return "error"
        val data = res.getJSONObject("data")
        return data.getString("index")
    }

    private var cookies : List<HttpCookie> = ArrayList()
    private fun post(url: String, data : Map<String, String>) : JSONObject{
        val con = Jsoup.connect(ip+url)
        cookies.forEach{ con.cookie(it.name, it.value) }
        con.ignoreContentType(true)
        con.data(data)
        val str = con.post().body().text()
        cookies = con.cookieStore().cookies
        return JSONObject.parseObject(str)
    }

    private fun get(url : String) : JSONObject{
        val con = Jsoup.connect(ip+url)
        cookies.forEach{ con.cookie(it.name, it.value) }
        con.ignoreContentType(true)
        val str = con.get().body().text()
        cookies = con.cookieStore().cookies
        return JSONObject.parseObject(str)
    }

    private fun encrypt(str: String, publicKey: String?): String {
        try {
            val decoded = Base64.decodeBase64(publicKey)
            val pubKey = KeyFactory.getInstance("RSA")
                .generatePublic(X509EncodedKeySpec(decoded)) as RSAPublicKey
            val cipher = Cipher.getInstance("RSA/None/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, pubKey)

            val bytes: ByteArray = str.toByteArray()
            val inputLen = bytes.size
            var offLen = 0 //偏移量

            var i = 0
            val bops = ByteArrayOutputStream()
            while (inputLen - offLen > 0) {
                val cache: ByteArray = if (inputLen - offLen > 117) {
                    cipher.doFinal(bytes, offLen, 117)
                } else {
                    cipher.doFinal(bytes, offLen, inputLen - offLen)
                }
                bops.write(cache)
                i++
                offLen = 117 * i
            }
            bops.close()
            val encryptedData: ByteArray = bops.toByteArray()
            return Base64.encodeBase64String(encryptedData)
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e.message)
        }
    }
}