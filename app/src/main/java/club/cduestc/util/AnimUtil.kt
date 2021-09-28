package club.cduestc.util

import android.view.View
import android.view.animation.Animation
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
        show(v, 0f, v.alpha)
    }

    fun show(v : View, start : Float, end : Float){
        v.alpha = 0f
        v.visibility = View.VISIBLE
        var anim = object : Animation(){
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.alpha = end * (start + interpolatedTime * (1 - start))
            }
        }
        anim.duration = 300
        anim.interpolator = LinearInterpolator()
        v.startAnimation(anim)
    }
}