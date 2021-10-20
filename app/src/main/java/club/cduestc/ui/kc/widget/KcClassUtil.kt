package club.cduestc.ui.kc.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import club.cduestc.R
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import java.util.*


object KcClassUtil {

    fun reloadWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray){
        val sharedPreference = context.getSharedPreferences("class_table", AppCompatActivity.MODE_PRIVATE)
        val arr = JSON.parseArray(sharedPreference.getString("class_table", "[]"))
        val ignore = JSONArray.parseArray(sharedPreference.getString("ignore", "[]"))
        val weekDate = Date(sharedPreference.getLong("current_week", getThisWeekMonday(Date()).time))
        val week = getWeekCount(weekDate, Date()) + sharedPreference.getInt("current_week_number", 1)

        for (i in appWidgetIds.indices){
            val views = RemoteViews(context.packageName, R.layout.kc_class_widget)
            views.setTextViewText(R.id.title, "第"+ mapToChinese(week.toInt())+"周   周"+ mapToChinese(getDay(Date())))
            val days = listOf(R.id.widget_class_day_1, R.id.widget_class_day_2, R.id.widget_class_day_3,
                R.id.widget_class_day_4, R.id.widget_class_day_5)
            days.forEach{ views.setViewVisibility(it, View.GONE) }
            arr.forEach {
                val obj = JSONObject.parseObject(it.toString())
                if(ignore.contains(obj.getString("name"))) return@forEach
                if(!obj.getJSONArray("weekSet").contains(week.toInt())) return@forEach
                if(obj.getIntValue("day") == getDay(Date())){
                    var index = obj.getJSONArray("indexSet").getIntValue(0)
                    index /= 2
                    Log.i("Widget", index.toString())
                    when(index){
                        0 -> setupClass(days[0], R.id.widget_class_name_1, R.id.widget_class_desc_1, obj, views)
                        1 -> setupClass(days[1], R.id.widget_class_name_2, R.id.widget_class_desc_2, obj, views)
                        2 -> setupClass(days[2], R.id.widget_class_name_3, R.id.widget_class_desc_3, obj, views)
                        3 -> setupClass(days[3], R.id.widget_class_name_4, R.id.widget_class_desc_4, obj, views)
                        4 -> setupClass(days[4], R.id.widget_class_name_5, R.id.widget_class_desc_5, obj, views)
                        else -> {}
                    }
                }
            }

            appWidgetManager.updateAppWidget(appWidgetIds[i], views)
        }
    }

    private fun setupClass(id1 : Int, id2 : Int, id3 : Int, obj : JSONObject, views : RemoteViews){
        views.setViewVisibility(id1, View.VISIBLE)
        views.setTextViewText(id2, obj.getString("name"))
        views.setTextViewText(id3, obj.getString("local")+" ("+obj.getString("teacher")+")")
    }

    private fun getDay(date: Date): Int {
        val cal = Calendar.getInstance()
        cal.time = date
        var weekIndex = cal[Calendar.DAY_OF_WEEK] - 1
        if (weekIndex < 0) weekIndex = 0
        return weekIndex
    }

    /**
     * 获取本周一的时间
     */
    fun getThisWeekMonday(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        val dayWeek = cal[Calendar.DAY_OF_WEEK]
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.firstDayOfWeek = Calendar.MONDAY
        val day = cal[Calendar.DAY_OF_WEEK]
        cal.add(Calendar.DATE, cal.firstDayOfWeek - day)
        return cal.time
    }

    /**
     * 获取周数间隔
     */
    fun getWeekCount(startDate: Date, endDate: Date) : Long {
        return (endDate.time - startDate.time) / (7 * 24 * 60 * 60 * 1000)
    }

    private fun mapToChinese(n : Int) : String{
        if(n == 20) return "二十"
        if(n >= 10) return "十" + mapToChinese(n%10)
        return when (n){
            1 -> "一"
            2 -> "二"
            3 -> "三"
            4 -> "四"
            5 -> "五"
            6 -> "六"
            7 -> "七"
            8 -> "八"
            9 -> "九"
            else -> ""
        }
    }
}