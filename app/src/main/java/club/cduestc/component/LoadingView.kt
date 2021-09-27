package club.cduestc.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*

class LoadingView(context : Context, attrs: AttributeSet?) : View(context, attrs) {

    private val timer = Timer()
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        timer.schedule(object : TimerTask() {
            override fun run(){
                if(orientation){
                    heightIn++
                    heightOut--
                    if(heightIn >= 100.0) orientation = false
                }else{
                    heightIn--
                    heightOut++
                    if(heightOut >= 100.0) orientation = true
                }
                invalidate()
            }
        }, 0L, 13L)
    }

    var heightOut = 100.0
    var heightIn = 70.0
    var orientation = true
    override fun onDraw(canvas: Canvas) {
        val oneWidth = width / 3.0F
        val spilt = oneWidth / 10.0F
        paint.color = Color.parseColor("#60D1AE")
        val topIn = height * (1 - (heightIn)/100.0) + spilt
        val bottomIn = height * (heightIn)/100.0 - spilt
        val topOut = height * (1 - (heightOut)/100.0) + spilt
        val bottomOut = height * (heightOut)/100.0 - spilt
        canvas.drawRoundRect(oneWidth * 0 + spilt, topOut.toFloat() , oneWidth * 1 - spilt, bottomOut.toFloat() - spilt, spilt, spilt, paint)
        canvas.drawRoundRect(oneWidth * 1 + spilt, topIn.toFloat() , oneWidth * 2 - spilt, bottomIn.toFloat(),spilt, spilt, paint)
        canvas.drawRoundRect(oneWidth * 2 + spilt, topOut.toFloat() , oneWidth * 3 - spilt, bottomOut.toFloat() - spilt,spilt, spilt, paint)
    }

    override fun onDetachedFromWindow() {
        timer.cancel()
        super.onDetachedFromWindow()
    }
}