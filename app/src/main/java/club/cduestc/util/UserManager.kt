package club.cduestc.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.alibaba.fastjson.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


object UserManager {

    private lateinit var data : JSONObject
    private var header : Bitmap? = null
    private var background : Bitmap? = null
    var kcPassword : String? = null

    fun init(data : JSONObject) {
        this.data = data
        header = getHttpBitmap(data.getString("headerUrl"))
        background = getHttpBitmap(data.getString("backgroundUrl"))
    }

    fun getIndex() : String{
        return data.getString("index")
    }

    fun getUserName(): String {
        return data.getString("username")
    }

    fun getEmail(): String {
        return data.getString("bindEmail")
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

    fun getBackground() : Bitmap?{
        return background
    }

    private fun getHttpBitmap(url: String?): Bitmap? {
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