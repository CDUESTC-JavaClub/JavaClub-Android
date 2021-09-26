package club.cduestc.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import androidx.cardview.widget.CardView
import club.cduestc.R
import kotlin.math.max

class CollapseCardView(context: Context, attrs: AttributeSet) : CardView(context, attrs) {

    var collapseHeight : Float
    var expandedHeight : Float
    var collapse = false

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CollapseCardView)
        collapseHeight = typedArray.getDimension(R.styleable.CollapseCardView_collapseHeight, 0f)
        expandedHeight = typedArray.getDimension(R.styleable.CollapseCardView_expandedHeight, 0f)
        collapse = typedArray.getBoolean(R.styleable.CollapseCardView_collapse, false)

        this.setOnClickListener {
            collapse = if(collapse){
                expand(this)
                false
            } else {
                collapse(this)
                true
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(collapse) this.layoutParams.height = collapseHeight.toInt()
    }

    private fun expand(view: View) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height = collapseHeight.toInt() + max(expandedHeight * interpolatedTime - collapseHeight, 0F).toInt()
                view.requestLayout()
            }
        }
        animation.duration = 400
        animation.interpolator = DecelerateInterpolator()
        view.startAnimation(animation)
    }

    private fun collapse(view: View) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height = collapseHeight.toInt() + max(expandedHeight * (1 - interpolatedTime) - collapseHeight, 0F).toInt()
                view.requestLayout()
            }
        }
        animation.duration = 400
        animation.interpolator = DecelerateInterpolator()
        view.startAnimation(animation)
    }
}