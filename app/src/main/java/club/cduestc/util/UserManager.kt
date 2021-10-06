package club.cduestc.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import club.byjh.entity.account.BaiAccount
import club.jw.auth.KcAccount
import com.alibaba.fastjson.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


object UserManager {

    private lateinit var data : JSONObject
    private var header : Bitmap? = null
    private var background : Bitmap? = null
    var index : String? = null
    lateinit var kcAccount : KcAccount
    lateinit var baiAccount : BaiAccount
    var oauthInfo : JSONObject? = null

    fun init(data : JSONObject) {
        this.data = data

        NetManager.createTask {
            val obj = JSONObject()
            try {
                val accounts = data.getJSONObject("connectedAccounts")
                if(accounts.containsKey("github")) obj["github"] = NetManager.getGithubInfo(accounts.getString("github"))
                if(accounts.containsKey("qq")) obj["qq"] = accounts.getJSONObject("qq")
                oauthInfo = obj
            }catch (e : Exception){
                e.printStackTrace()
                oauthInfo = obj
            }
        }
    }

    fun initImage(u1 : String?, u2 : String?){
        header = NetManager.getHttpBitmap(u1)
        background = NetManager.getHttpBitmap(u2)
    }

    fun getUserName(): String {
        return data.getString("username")
    }

    fun getEmail(): String {
        return data.getString("email")
    }

    fun getHeader() : Bitmap?{
        return header
    }

    fun getSignature() : String {
        return data.getString("signature")
    }

    fun getBindId() : String? {
        return data.getString("bindId")
    }

    fun setBindId(id : String){
        data.put("bindId", id)
    }

    fun getBackground() : Bitmap?{
        return background
    }
}