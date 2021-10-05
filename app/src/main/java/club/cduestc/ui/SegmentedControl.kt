package club.cduestc.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import club.cduestc.R
import club.cduestc.util.AnimUtil

@SuppressLint("ClickableViewAccessibility")
class SegmentedControl(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var radius : Float
    private var border : Float
    private var fontSize : Float
    private val paint = Paint()
    private val tabList = LinkedHashMap<String, View>()
    private var selectIndex = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SegmentedControl)
        radius = typedArray.getDimension(R.styleable.SegmentedControl_radius, 0f)
        border = typedArray.getDimension(R.styleable.SegmentedControl_border, 0f)
        fontSize = typedArray.getDimension(R.styleable.SegmentedControl_textSize, 0f)
        typedArray.recycle()

        this.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                val tabWidth : Float = measuredWidth.toFloat() / tabList.size
                val index = (event.x / tabWidth).toInt()
                if(index == selectIndex) return@setOnTouchListener false
                val keys = tabList.keys.toTypedArray()
                AnimUtil.hide(tabList[keys[selectIndex]]!!, 150)
                select(index)
                AnimUtil.show(tabList[keys[index]]!!, 0f , 1f, 150)
            }
            return@setOnTouchListener false;
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.parseColor("#60D1AE")
        paint.strokeWidth = border
        paint.style = Paint.Style.STROKE
        canvas.drawRoundRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), radius, radius, paint)

        val tabWidth : Float = measuredWidth.toFloat() / tabList.size
        paint.style = Paint.Style.FILL
        paint.strokeWidth = border / 4
        for (i in 1 until tabList.size) {
            canvas.drawRect(i * tabWidth, 0f, i * tabWidth + border, measuredHeight.toFloat(), paint)
        }
        canvas.drawRoundRect(selectIndex * tabWidth, 0f, (selectIndex + 1) * tabWidth, measuredHeight.toFloat(), radius, radius, paint)
        if(selectIndex + 1 < tabList.size){
            canvas.drawRect((selectIndex + 1) * tabWidth - radius, 0f, (selectIndex + 1) * tabWidth, measuredHeight.toFloat(), paint)
        }
        if(selectIndex + 1 <= tabList.size && selectIndex != 0){
            canvas.drawRect(selectIndex* tabWidth, 0f, selectIndex * tabWidth + radius, measuredHeight.toFloat(), paint)
        }

        for (i in 0 until tabList.size){
            if(i != selectIndex){
                if(context.resources.configuration.uiMode == 0x21) paint.color = Color.parseColor("#FFFFFF")
                else paint.color = Color.parseColor("#444444")
            }else{
                if(context.resources.configuration.uiMode == 0x21) paint.color = Color.parseColor("#444444")
                else paint.color = Color.parseColor("#FFFFFF")
            }
            paint.textSize = 40f
            val name : String = tabList.keys.toTypedArray()[i]
            val rect = Rect()
            paint.getTextBounds(name, 0, name.length, rect)
            canvas.drawText(name, i * tabWidth + tabWidth/2 - rect.width()/2, (measuredHeight/2 + rect.height()/2).toFloat(), paint)
        }
    }

    fun addTab(name : String, view: View){
        tabList[name] = view
    }

    private fun select(index : Int){
        this.selectIndex = index
        this.invalidate()
    }
}