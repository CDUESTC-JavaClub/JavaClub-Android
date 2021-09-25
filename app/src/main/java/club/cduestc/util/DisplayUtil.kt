package club.cduestc.util

import android.content.Context
import android.util.TypedValue
import android.view.Window


object DisplayUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dpWindowSize(window: Window?, context: Context, width : Int, height : Int) {
        if(window == null) return
        val scale: Float = context.resources.displayMetrics.density
        val pxWidth = dp2px(context, width)
        val pxHeight = dp2px(context, height)
        window.setLayout(pxWidth, pxHeight)
    }

    fun dp2px(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}