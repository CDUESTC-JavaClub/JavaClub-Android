package club.cduestc.ui.kc.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import club.cduestc.R
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs

class KcClassWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        KcClassUtil.reloadWidget(context, appWidgetManager, appWidgetIds)
    }
}