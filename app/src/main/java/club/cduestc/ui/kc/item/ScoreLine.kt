package club.cduestc.ui.kc.item

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import club.cduestc.R

class ScoreLine(context: Context?,
                private val desc: String,
                private val name: String,
                private val score: String
) : RelativeLayout(context) {

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.score_line, this)
        val number = view.findViewById<TextView>(R.id.score_number)
        number.text = if(score == "100.0") "100" else score
        val color : String
        if(score.contains(Regex("[0-9]"))) {
             val nScore = score.toDouble()
            color = when {
                nScore < 60 -> "#DC143C"
                nScore >= 60 && nScore <70 -> "#FF8C00"
                nScore >= 70 && nScore <80 -> "#BA55D3"
                nScore >= 80 && nScore <90 -> "#1E90FF"
                nScore >= 90 -> "#32CD32"
                else -> "#DC143C"
            }
        }else{
            color = when(score) {
                "通过" -> "#FF8C00"
                "中等" -> "#BA55D3"
                "良好" -> "#1E90FF"
                "优秀" -> "#32CD32"
                else -> "#DC143C"
            }
        }
        val background = view.findViewById<CardView>(R.id.score_background)
        background.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
        val name = view.findViewById<TextView>(R.id.score_name)
        name.text = this.name
        val desc = view.findViewById<TextView>(R.id.score_desc)
        desc.text = this.desc
    }
}