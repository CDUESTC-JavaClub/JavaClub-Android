package club.cduestc.ui.kc.sub

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import club.cduestc.R
import club.cduestc.ui.kc.item.ClassCard
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import java.util.*
import kotlin.math.abs
import android.widget.Toast

import android.content.DialogInterface
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


class KcTableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kc_table)

        window.navigationBarColor = Color.TRANSPARENT

        val sharedPreference = getSharedPreferences("data", MODE_PRIVATE)
        this.initClassTable(sharedPreference.getInt("class_term", 1))
    }

    private fun switchTerm(it : View){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, android.R.style.ThemeOverlay_Material_Dialog)
        builder.setTitle("请选择学期")
        val cities = this.genTerms().toTypedArray()
        builder.setItems(cities) { _, which ->
            val sharedPreference = getSharedPreferences("data", MODE_PRIVATE)
            sharedPreference.edit().remove("class_table").apply()
            initClassTable(which + 1)
        }
        builder.show()
    }

    private fun genTerms() : List<String>{
        val date = UserManager.kcAccount.info.get("入校时间") as Date
        val format = SimpleDateFormat("yyyy")
        val startYear = format.format(date).toInt()
        val list = LinkedList<String>()
        if(UserManager.kcAccount.info.get("学历层次") == "本科"){
            for (i in 0..3){
                list.add("本科 ${startYear+i}-${startYear + 1 + i} 学年（上）")
                list.add("本科 ${startYear+i}-${startYear + 1 + i} 学年（下）")
            }
        }else{
            for (i in 0..2){
                list.add("专科 ${startYear+i}-${startYear + 1 + i} 学年（上）")
                list.add("专科 ${startYear+i}-${startYear + 1 + i} 学年（下）")
            }
        }
        return list
    }

    private fun initClassTable(term : Int){
        findViewById<ProgressBar>(R.id.class_loading).visibility = View.VISIBLE
        val sharedPreference = getSharedPreferences("data", MODE_PRIVATE)
        val btn = findViewById<Button>(R.id.kc_class_switch)
        btn.setOnClickListener(this::switchTerm)
        sharedPreference.edit().putInt("class_term", term).apply()
        btn.text = genTerms()[sharedPreference.getInt("class_term", term) - 1]

        val matrix : Array<Array<ClassCard?>> = Array(5) { Array(7){null} }
        val rows = listOf(
            findViewById<TableRow>(R.id.row1),
            findViewById(R.id.row2),
            findViewById(R.id.row3),
            findViewById(R.id.row4),
            findViewById(R.id.row5)
        )
        rows.forEach { it.removeAllViews() }
        colorHash = Array(20){null}

        if(!sharedPreference.contains("class_table")){
            NetManager.createTask{
                val arr =  UserManager.kcAccount.getClassTable(term).toJSONArray()
                sharedPreference.edit().putString("class_table", arr.toString()).apply()
                addCards(arr, rows, matrix)
                runOnUiThread { fillTable(rows, matrix) }
            }
        }else{
            val arr = JSON.parseArray(sharedPreference.getString("class_table", "[]"))
            addCards(arr, rows, matrix)
            fillTable(rows, matrix)
        }
    }

    private fun fillTable(rows : List<TableRow>,  matrix: Array<Array<ClassCard?>>){
        for (i in 0..4){
            val row = rows[i]
            for (j in 0..6){
                if(matrix[i][j] == null){
                    row.addView(ClassCard(this, null, null, null), j)
                }else{
                    row.addView(matrix[i][j], j)
                }
            }
        }
        findViewById<ProgressBar>(R.id.class_loading).visibility = View.GONE
    }

    private fun addCards(arr: JSONArray, rows: List<TableRow>, matrix: Array<Array<ClassCard?>>){
        arr.forEach {
            val item = JSONObject.parseObject(it.toString())
            var index = item.getJSONArray("indexSet").getIntValue(0)
            index /= 2
            val day = item.getIntValue("day")
            val name = item.getString("name")
            matrix[index][day - 1] = ClassCard(this, item, calTime(index), colorSelect(name))
        }
    }

    /**
     * 时间计算
     */
    private fun calTime(index : Int) : String{
        return when(index){
            0 -> "8:15"
            1 -> "10:05"
            2 -> "14:00"
            3 -> "15:50"
            4 -> "18:30"
            else -> "0:00"
        }
    }

    /* 颜色表 */
    private val colorList = listOf("#CCCD5C5C", "#CCFFA500", "#CCEEDD82", "#CC00FF7F", "#CC63B8FF", "#CC436EEE", "#CCFFE4C4",
        "#CCD15FEE", "#CCFFA54F", "#CCCDBA96", "#CC32CD32", "#CC32CD32", "#CC20B2AA", "#CCFFAEB9", "#CC9370DB", "#CC00BFFF",
        "#CC00FA9A", "#CCEECFA1", "#CCF0E68C", "#CCFF69B4")
    /* 暂定Hash表长度最大为20，不会吧不会吧，不会还有一学期20多门课的吧？ */
    private var colorHash : Array<String?> = Array(20){null}
    /**
     * 多重哈希随机颜色匹配算法
     *
     * @param name 课程名称
     * @return 颜色代码
     */
    private fun colorSelect(name : String) : String{
        var code = abs(name[0].code % 20)
        while (colorHash[code] != null && colorHash[code] != name) code = (code + 1) % 20
        colorHash[code] = name
        return colorList[code]
    }
}