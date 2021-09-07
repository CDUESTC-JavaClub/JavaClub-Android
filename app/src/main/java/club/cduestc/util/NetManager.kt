package club.cduestc.util

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
import java.util.concurrent.Executors
import javax.crypto.Cipher
import kotlin.collections.ArrayList


object NetManager {

    private val version = "0.0.3"
    private var ip = "http://api.cduestc.club/api"
    private var executorService = Executors.newFixedThreadPool(10)

    fun createTask(task : Runnable){
        executorService.execute(task)
    }

    fun login(name: String, pwd: String) : Boolean {
        cookies = null  //清理缓存
        val getRes = get("/auth/public-key") ?: return false
        val key = getRes.getString("data")
        val req = mapOf("id" to name, "password" to encrypt(pwd, key), "remember-me" to "false")
        val res = post("/auth/login", req) ?: return false
        if (res.getIntValue("status") != 200) return false
        if(res.getBoolean("data")){
            val getResp = get("/auth/info") ?: return false
            UserManager.init(getResp.getJSONObject("data"))
        }
        return res.getBoolean("data")
    }

    fun initForum() : JSONObject?{
        val getResp = get("/auth/forum") ?: return null
        return getResp.getJSONObject("data")
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

    fun getVersion() : String{
        return version
    }

    fun update() : JSONObject?{
        return get("/check/update")
    }

    private var cookies : List<HttpCookie>? = null
    private fun post(url: String, data : Map<String, String>) : JSONObject?{
        return try {
            val con = Jsoup.connect(ip+url)
            con.timeout(10000)
            cookies?.forEach{ con.cookie(it.name, it.value) }
            con.ignoreContentType(true)
            con.data(data)
            val str = con.post().body().text()
            if(cookies == null) cookies = ArrayList(con.cookieStore().cookies)
            JSONObject.parseObject(str)
        }catch (e : IOException){
            e.printStackTrace()
            null
        }
    }

    private fun get(url : String) : JSONObject?{
        return try{
            val con = Jsoup.connect(ip+url)
            con.timeout(10000)
            cookies?.forEach{ con.cookie(it.name, it.value) }
            con.ignoreContentType(true)
            val str = con.get().body().text()
            if(cookies == null) cookies = ArrayList(con.cookieStore().cookies)
            JSONObject.parseObject(str)
        }catch (e : IOException){
            null
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