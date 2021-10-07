package club.cduestc.ui.contest.item

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.ceil


class AutoHeightImageView(context : Context, attrs : AttributeSet) : AppCompatImageView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (drawable != null) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height =
                ceil((width.toFloat() * drawable.intrinsicHeight.toFloat() / drawable.intrinsicWidth
                    .toFloat()).toDouble())
                    .toInt()
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

}