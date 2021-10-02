package club.cduestc.util

import android.content.SharedPreferences
import android.util.Log
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import org.jsoup.Jsoup
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.RuntimeException
import java.net.HttpCookie
import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.crypto.Cipher
import kotlin.collections.ArrayList


object NetManager {
    private var ip = "https://api.cduestc.club/api"
    private var executorService = Executors.newFixedThreadPool(10)

    fun isBaiNetwork() : Boolean{
        try {
            val resp = Jsoup.connect("http://byjh.cduestc.cn/").get()
            return !resp.body().text().contains("403")
        }catch (e : Exception){
            return false
        }
    }

    fun ocr(image : String) : String{
        val resp = post("/auth/ocr", mapOf("image" to image))
        val str = resp!!.getString("data")
        Log.i("OCR", "自动识别到验证码：$str")
        return str
    }

    fun createTask(task : Runnable){
        executorService.execute(task)
    }

    fun login(name: String, pwd: String) : Boolean {
        cookies.clear()  //清理缓存
        val getRes = get("/auth/public-key") ?: return false
        val key = getRes.getString("data")
        val req = mapOf("id" to name, "password" to encrypt(pwd, key), "remember-me" to "true")
        val res = post("/auth/login", req) ?: return false
        if (res.getIntValue("status") != 200) return false
        return res.getBoolean("data")
    }

    fun initUserInfo() : Boolean{
        val getResp = get("/auth/info") ?: return false
        if (getResp.getIntValue("status") != 200) return false
        UserManager.init(getResp.getJSONObject("data"))
        return true
    }

    fun getGithubInfo(id : String) : JSONObject?{
        return try {
            val connect = Jsoup.connect("https://api.github.com/user/$id")
            connect.ignoreContentType(true)
            JSONObject.parseObject(connect.get().text())
        }catch (e : Exception){
            null
        }
    }

    fun initForum() : JSONObject?{
        val getResp = get("/auth/forum") ?: return null
        return if(getResp.getInteger("status") == 200){
            getResp.getJSONObject("data")
        } else null
    }

    fun allContest() : JSONArray?{
        val getResp = get("/contest/all") ?: return null
        return if(getResp.getInteger("status") == 200){
            getResp.getJSONArray("data")
        } else null
    }

    fun bind(id: String, pwd : String) : Boolean {
        val getRes = get("/auth/public-key") ?: return false
        val key = getRes.getString("data")
        val map = mapOf("id" to id, "password" to encrypt(pwd, key))
        val res = post("/auth/bind-id", map) ?: return false
        if (res.getIntValue("status") != 200) return false
        return res.getBoolean("data")
    }

    fun logout(){
        get("/auth/logout")
    }

    fun update() : JSONObject?{
        return get("/check/update")
    }

    fun oauth(url : String) : Boolean{
        return try{
            val con = Jsoup.connect(url)
            con.ignoreContentType(true)
            con.timeout(60000)
            val str = con.get().body().text()
            cookies = ArrayList(con.cookieStore().cookies)
            val res = JSONObject.parseObject(str)
            if(res.getInteger("status") != 200) return false
            return res.getBoolean("data")
        }catch (e : Exception){
            e.printStackTrace()
            false
        }
    }

    fun cookieWrite(sharedPreference : SharedPreferences){
        val obj = JSONObject()
        cookies.forEach { obj[it.name] = it.value }
        sharedPreference.edit().putString("saved_cookie", obj.toJSONString()).apply()
    }

    fun cookieRead(sharedPreference : SharedPreferences){
        val obj = JSONObject.parseObject(sharedPreference.getString("saved_cookie", "{}"))
        cookies = ArrayList()
        obj.forEach { k, v -> cookies.add(HttpCookie(k, v.toString())) }
    }

    private var cookies : ArrayList<HttpCookie> = ArrayList()
    private fun post(url: String, data : Map<String, String>) : JSONObject?{
        return try {
            val con = Jsoup.connect(ip+url)
            con.timeout(20000)
            cookies.forEach{ con.cookie(it.name, it.value) }
            con.ignoreContentType(true)
            con.data(data)
            val str = con.post().body().text()
            updateCookie(con.cookieStore().cookies)
            JSONObject.parseObject(str)
        }catch (e : IOException){
            e.printStackTrace()
            null
        }
    }

    private fun get(url : String) : JSONObject?{
        return try{
            val con = Jsoup.connect(ip+url)
            con.timeout(20000)
            cookies.forEach{ con.cookie(it.name, it.value) }
            con.ignoreContentType(true)
            val str = con.get().body().text()
            updateCookie(con.cookieStore().cookies)
            JSONObject.parseObject(str)
        }catch (e : Exception){
            e.printStackTrace()
            null
        }
    }

    private fun updateCookie(list : List<HttpCookie>){
        list.forEach {
            if(!cookies.contains(it)) cookies.add(it)
            else {
                cookies.remove(it)
                cookies.add(it)
            }
        }
    }

    private fun encrypt(str: String, publicKey: String?): String {
        try {
            val decoded = Base64.getDecoder().decode(publicKey)
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
            return Base64.getEncoder().encodeToString(encryptedData)
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e.message)
        }
    }
}