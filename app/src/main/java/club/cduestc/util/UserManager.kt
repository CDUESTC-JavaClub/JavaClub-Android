package club.cduestc.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

    fun init(data : JSONObject) {
        this.data = data
    }

    fun initImage(u1 : String?, u2 : String?){
        header = getHttpBitmap(u1)
        background = getHttpBitmap(u2)
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

    fun getHttpBitmap(url: String?): Bitmap? {
        val myFileURL: URL
        var bitmap: Bitmap? = null
        try {
            myFileURL = URL(url)
            val conn: HttpURLConnection = myFileURL.openConnection() as HttpURLConnection
            conn.connectTimeout = 6000
            conn.doInput = true
            conn.useCaches = false
            val inputStream: InputStream = conn.inputStream
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }
}