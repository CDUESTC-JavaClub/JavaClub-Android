package club.cduestc.util

import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation

object AnimUtil {
    fun hide(v : View){
        val alphaOrg = v.alpha
        var anim = object : Animation(){
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.alpha = alphaOrg * (1 - interpolatedTime)
                if(v.alpha <= 0f){
                    v.visibility = View.GONE
                }
            }
        }
        anim.duration = 300
        anim.interpolator = LinearInterpolator()
        v.startAnimation(anim)
    }

    fun show(v : View){
        val alphaOrg = v.alpha
        v.alpha = 0f
        v.visibility = View.VISIBLE
        var anim = object : Animation(){
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.alpha = alphaOrg * interpolatedTime
            }
        }
        anim.duration = 300
        anim.interpolator = LinearInterpolator()
        v.startAnimation(anim)

    }
}