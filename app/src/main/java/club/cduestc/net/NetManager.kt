package club.cduestc.net

import com.alibaba.fastjson.JSONObject
import org.apache.commons.codec.binary.Base64
import org.jsoup.Jsoup
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.RuntimeException
import java.net.HttpCookie
import java.net.SocketTimeoutException
import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.Executors
import javax.crypto.Cipher
import kotlin.collections.ArrayList


object NetManager {

    private var ip = "http://192.168.10.3:8080/api"
    private var executorService = Executors.newFixedThreadPool(10)

    fun createTask(task : Runnable){
        executorService.execute(task)
    }

    fun login(name: String, pwd: String) : Boolean {
        val getRes = get("/auth/public-key") ?: return false
        val key = getRes.getString("data");
        val req = mapOf("id" to name, "password" to encrypt(pwd, key), "remember-me" to "true")
        val res = post("/auth/login", req) ?: return false
        if (res.getIntValue("status") != 200) return false
        UserManager.init(res.getJSONObject("data"))
        return true
    }

    private var cookies : List<HttpCookie> = ArrayList()
    private fun post(url: String, data : Map<String, String>) : JSONObject?{
        return try {
            val con = Jsoup.connect(ip+url)
            con.timeout(5000)
            cookies.forEach{ con.cookie(it.name, it.value) }
            con.ignoreContentType(true)
            con.data(data)
            val str = con.post().body().text()
            cookies = con.cookieStore().cookies
            JSONObject.parseObject(str)
        }catch (e : IOException){
            null
        }
    }

    private fun get(url : String) : JSONObject?{
        return try{
            val con = Jsoup.connect(ip+url)
            con.timeout(5000)
            cookies.forEach{ con.cookie(it.name, it.value) }
            con.ignoreContentType(true)
            val str = con.get().body().text()
            cookies = con.cookieStore().cookies
            JSONObject.parseObject(str)
        }catch (e : IOException){
            null
        }
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