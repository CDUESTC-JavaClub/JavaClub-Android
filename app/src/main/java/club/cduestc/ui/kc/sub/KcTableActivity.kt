package club.cduestc.ui.kc.sub

import android.app.AlertDialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.core.view.get
import club.cduestc.R
import club.cduestc.ui.kc.item.ClassCard
import club.cduestc.ui.kc.item.WeekSelect
import club.cduestc.ui.kc.widget.KcClassUtil
import club.cduestc.ui.kc.widget.KcClassWidget
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class KcTableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kc_table)

        window.navigationBarColor = Color.TRANSPARENT

        val sharedPreference = getSharedPreferences("class_table", MODE_PRIVATE)
        this.loadWeekChooser();
        this.initClassTable(sharedPreference.getInt("class_term", 1))
    }

    private fun loadWeekChooser(){
        val view = findViewById<LinearLayout>(R.id.week_selector)
        for (i in 1..20) view.addView(WeekSelect(this, i, view.children){})
    }

    /**
     * 切换单双周
     */
    private fun switchWeek(it : View){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, android.R.style.ThemeOverlay_Material_Dialog)
        builder.setTitle(getString(R.string.kc_class_week_select))
        builder.setItems(arrayOf(getString(R.string.kc_class_week_odd), getString(R.string.kc_class_week_dual))) { _, which ->
            var week = getCurrentWeek()
            if(which != 0) week -= 1
            val sharedPreference = getSharedPreferences("class_table", MODE_PRIVATE)
            sharedPreference.edit().putInt("single_week", week).apply()
            initClassTable(sharedPreference.getInt("class_term", 1))
        }
        builder.show()
    }

    private fun switchTerm(it : View){
        if(findViewById<View>(R.id.class_loading).visibility == View.VISIBLE) return
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, android.R.style.ThemeOverlay_Material_Dialog)
        builder.setTitle(getString(R.string.kc_class_term_select))
        val cities = this.genTerms().toTypedArray()
        builder.setItems(cities) { _, which ->
            val sharedPreference = getSharedPreferences("class_table", MODE_PRIVATE)
            sharedPreference.edit().remove("class_table").remove("ignore").apply()
            initClassTable(which + 1)
        }
        builder.show()
    }

    private fun genTerms() : List<String>{
        val date = UserManager.kcAccount.info.get("入校时间") as Date
        val format = SimpleDateFormat("yyyy", Locale.CHINA)
        val startYear = format.format(date).toInt()
        val list = LinkedList<String>()
        if(UserManager.kcAccount.info.get("学历层次") == "本科"){
            for (i in 0..3){
                list.add(getString(R.string.kc_class_level_b_1, startYear+i, startYear + 1 + i))
                list.add(getString(R.string.kc_class_level_b_2, startYear+i, startYear + 1 + i))
            }
        }else{
            for (i in 0..2){
                list.add(getString(R.string.kc_class_level_z_1, startYear+i, startYear + 1 + i))
                list.add(getString(R.string.kc_class_level_z_2, startYear+i, startYear + 1 + i))
            }
        }
        return list
    }

    private fun initClassTable(term : Int){
        findViewById<View>(R.id.class_loading).visibility = View.VISIBLE
        val sharedPreference = getSharedPreferences("class_table", MODE_PRIVATE)
        val btn = findViewById<Button>(R.id.kc_class_switch)
        btn.setOnClickListener(this::switchTerm)
        sharedPreference.edit().putInt("class_term", term).apply()
        btn.text = genTerms()[sharedPreference.getInt("class_term", term) - 1]

        val matrix : Array<Array<ArrayList<ClassCard>>> = Array(5) { Array(7){ArrayList()} }
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
                var arr = JSONArray()
                try {
                    arr =  UserManager.kcAccount.getClassTable(term).toJSONArray()
                }catch (e : Exception){
                    this.runOnUiThread { Toast.makeText(this, getString(R.string.kc_account_timeout), Toast.LENGTH_SHORT).show() }
                    for (i in 1..10){
                        try {
                            UserManager.kcAccount.login()
                            arr =  UserManager.kcAccount.getClassTable(term).toJSONArray()
                            break
                        }catch (e : Exception){
                            e.printStackTrace()
                            if(i == 9) this.runOnUiThread {
                                Toast.makeText(this, getString(R.string.kc_class_unknown_error), Toast.LENGTH_SHORT).show()
                                this.finish()
                            }
                        }
                    }
                }
                sharedPreference.edit().putString("class_table", arr.toString()).apply()
                addCards(arr, matrix)
                runOnUiThread { fillTable(rows, matrix) }
            }
        }else{
            val arr = JSON.parseArray(sharedPreference.getString("class_table", "[]"))
            addCards(arr, matrix)
            fillTable(rows, matrix)
        }
    }

    private fun fillTable(rows : List<TableRow>,  matrix : Array<Array<ArrayList<ClassCard>>>){
        for (i in 0..4){
            val row = rows[i]
            for (j in 0..6){
                if(matrix[i][j].isEmpty()){
                    row.addView(ClassCard(this, null, null, null, this), j)
                }else{
                    var clazz = ClassCard(this, null, null, null, this)
                    matrix[i][j].forEach {
                        if(KcClassUtil.isThisWeekClass(it.clazz, this)) clazz = it
                    }
                    row.addView(clazz, j)
                }
            }
        }
        findViewById<View>(R.id.class_loading).visibility = View.GONE

        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val componentName = ComponentName(applicationContext, KcClassWidget::class.java)
        KcClassUtil.reloadWidget(this, appWidgetManager, appWidgetManager.getAppWidgetIds(componentName))
    }

    private fun addCards(arr: JSONArray, matrix: Array<Array<ArrayList<ClassCard>>>){
        val sharedPreference = getSharedPreferences("class_table", MODE_PRIVATE)
        val ignore = JSONArray.parseArray(sharedPreference.getString("ignore", "[]"))
        arr.forEach {
            val item = JSONObject.parseObject(it.toString())
            var index = item.getJSONArray("indexSet").getIntValue(0)
            index /= 2
            val day = item.getIntValue("day")
            val name = item.getString("name")
            if(ignore.contains(name)) return@forEach
            if(index < 5) matrix[index][day - 1].add(ClassCard(this, item, calTime(index), colorSelect(name), this))
        }
    }

    private fun getCurrentWeek(): Int {
        val g = GregorianCalendar()
        g.time = Date()
        return g[Calendar.WEEK_OF_YEAR]
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