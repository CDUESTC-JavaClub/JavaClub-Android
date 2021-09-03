package club.cduestc.ui.kc.sub

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import club.cduestc.R
import club.cduestc.ui.kc.item.ClassCard
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import java.util.*

class KcTableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kc_table)

        this.initClassTable()
    }

    private fun initClassTable(){

        val rows = Arrays.asList(findViewById<TableRow>(R.id.row1), findViewById<TableRow>(R.id.row2),
            findViewById<TableRow>(R.id.row3), findViewById<TableRow>(R.id.row4), findViewById<TableRow>(R.id.row5))
        rows.forEach(this::nullTable)

        val sharedPreference = getSharedPreferences("data", MODE_PRIVATE)
        if(!sharedPreference.contains("class_table")){
            NetManager.createTask{
                val arr =  UserManager.kcAccount.getClassTable(0).toJSONArray()
                sharedPreference.edit().putString("class_table", arr.toString()).apply()
                runOnUiThread {
                    arr.forEach {
                        val item = JSONObject.parseObject(it.toString())
                        var index = item.getJSONArray("indexSet").getIntValue(0)
                        index = index / 2 + 1
                        val row = rows[index]
                        val day = item.getIntValue("day")
                        row.removeViewAt(day)
                        row.addView(ClassCard(this, item.getString("name"), "#214782"), day)
                    }
                }
            }
        }else{
            val arr = JSON.parseArray(sharedPreference.getString("class_table", "[]"))
        }
    }

    private fun nullTable(row : TableRow){
        for (i in 1..7) row.addView(ClassCard(this, "", null))
    }

    private fun addCards(obj : JSONObject){
        val row1 = findViewById<TableLayout>(R.id.class_table)


    }

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}