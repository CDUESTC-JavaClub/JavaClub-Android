package club.cduestc.ui.kc.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.view.View
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import club.cduestc.R
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import java.util.*

object KcClassUtil {

    fun reloadWidget(applicationContext : Context){
        val sharedPreference = applicationContext.getSharedPreferences("class_table",
            AppCompatActivity.MODE_PRIVATE
        )
        val arr = JSON.parseArray(sharedPreference.getString("class_table", "[]"))
        val views = RemoteViews(applicationContext.packageName, R.layout.kc_class_widget)

        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val componentName = ComponentName(applicationContext, KcClassWidget::class.java)
        val days = listOf(R.id.widget_class_day_1, R.id.widget_class_day_2, R.id.widget_class_day_3,
            R.id.widget_class_day_4, R.id.widget_class_day_5)
        days.forEach{ views.setViewVisibility(it, View.GONE) }
        arr.forEach {
            val obj = JSONObject.parseObject(it.toString())
            if(obj.getIntValue("day") == 1){
                var index = obj.getJSONArray("indexSet").getIntValue(0)
                index /= 2
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
        appWidgetManager.updateAppWidget(componentName, views)
    }

    private fun setupClass(id1 : Int, id2 : Int, id3 : Int, obj : JSONObject, views : RemoteViews){
        views.setViewVisibility(id1, View.VISIBLE)
        views.setTextViewText(id2, obj.getString("name"))
        views.setTextViewText(id3, obj.getString("local")+" ("+obj.getString("teacher")+")")
    }

    private fun getWeek(date: Date): Int {
        val cal = Calendar.getInstance()
        cal.time = date
        var weekIndex = cal[Calendar.DAY_OF_WEEK] - 1
        if (weekIndex < 0) weekIndex = 0
        return weekIndex
    }
}