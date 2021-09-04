package club.cduestc.ui.kc.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import club.cduestc.R

/**
 * Implementation of App Widget functionality.
 */
class KcClassWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.kc_class_widget)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun calTime(index : Int) : String{
        return when(index){
            0 -> "08:15"
            1 -> "10:05"
            2 -> "14:00"
            3 -> "15:50"
            4 -> "18:30"
            else -> "0:00"
        }
    }
}