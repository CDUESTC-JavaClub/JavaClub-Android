package club.cduestc.util

import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import androidx.appcompat.app.AppCompatDelegate
import java.time.Duration

object AnimUtil {

    fun switchDayNight(dark : Boolean){
        if (dark){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun hide(v : View){
        hide(v, 300)
    }

    fun hide(v : View, duration: Long){
        val alphaOrg = v.alpha
        var anim = object : Animation(){
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.alpha = alphaOrg * (1 - interpolatedTime)
                if(v.alpha <= 0f){
                    v.visibility = View.GONE
                }
            }
        }
        anim.duration = duration
        anim.interpolator = LinearInterpolator()
        v.startAnimation(anim)
    }

    fun show(v : View){
        show(v, 0f, v.alpha)
    }

    fun show(v : View, start : Float, end : Float){
        show(v, start, end, 300)
    }

    fun show(v : View, start : Float, end : Float, duration: Long){
        v.alpha = 0f
        v.visibility = View.VISIBLE
        var anim = object : Animation(){
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.alpha = end * (start + interpolatedTime * (1 - start))
            }
        }
        anim.duration = duration
        anim.interpolator = LinearInterpolator()
        v.startAnimation(anim)
    }
}