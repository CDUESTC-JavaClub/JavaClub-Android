package club.cduestc.net

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

object UserManager {

    private lateinit var data : JSONObject

    fun init(data : JSONObject) {
        this.data = data
    }

    fun getIndex() : String{
        return data.getString("index")
    }
}