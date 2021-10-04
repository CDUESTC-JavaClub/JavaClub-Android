package club.cduestc.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import androidx.cardview.widget.CardView
import club.cduestc.R
import kotlin.math.max

class CollapseCardView : CardView {

    var collapseHeight : Float
    var expandedHeight : Float? = 0f
    var collapse = false
    var first = true

    constructor(context: Context) : super(context){
        collapseHeight = 0f
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CollapseCardView)
        collapseHeight = typedArray.getDimension(R.styleable.CollapseCardView_collapseHeight, 0f)
        expandedHeight = typedArray.getDimension(R.styleable.CollapseCardView_expandHeight, 0f)
        collapse = typedArray.getBoolean(R.styleable.CollapseCardView_collapse, false)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int) : super(context, attrs, defStyleAttr){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CollapseCardView)
        collapseHeight = typedArray.getDimension(R.styleable.CollapseCardView_collapseHeight, 0f)
        expandedHeight = typedArray.getDimension(R.styleable.CollapseCardView_expandHeight, 0f)
        collapse = typedArray.getBoolean(R.styleable.CollapseCardView_collapse, false)
    }

    fun collapse(){
        collapse(this)
        collapse = true
    }

    fun toggle(){
        collapse = if(collapse){
            expand(this)
            false
        } else {
            collapse(this)
            true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(first){
            if(collapse) this.layoutParams.height = collapseHeight.toInt()
            if(expandedHeight == 0f) expandedHeight = measuredHeight.toFloat()
            first = false
        }
    }

    private fun expand(view: View) {
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height = collapseHeight.toInt() + max(expandedHeight !!* interpolatedTime - collapseHeight, 0F).toInt()
                view.requestLayout()
            }
        }
        animation.duration = 400
        animation.interpolator = DecelerateInterpolator()
        view.startAnimation(animation)
    }

    private fun collapse(view: View) {
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height = collapseHeight.toInt() + max(expandedHeight !!* (1 - interpolatedTime) - collapseHeight, 0F).toInt()
                view.requestLayout()
            }
        }
        animation.duration = 400
        animation.interpolator = DecelerateInterpolator()
        view.startAnimation(animation)
    }
}