package club.cduestc.ui.bai.item

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import club.byjh.entity.activity.Activity
import club.byjh.entity.score.ScoreAdd
import club.cduestc.R

class ScoreLine(context: Context,
                scoreAdd: ScoreAdd
) : ConstraintLayout(context) {
    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.bai_activity_add_line, this)
        view.findViewById<TextView>(R.id.score_name).text = scoreAdd.name
        val color = when(scoreAdd.type){
            "博学" -> "#AF4C4C"
            "笃行" -> "#4CAF50"
            "尽美" -> "#FFC107"
            "明德" -> "#078BFF"
            else -> "#AF4C4C"
        }
        val value = view.findViewById<TextView>(R.id.score_value)
        value.text = "+"+scoreAdd.add
        value.setTextColor(Color.parseColor(color))
        view.findViewById<TextView>(R.id.score_type).text = when(scoreAdd.type){
            "博学" -> context.getString(R.string.bai_name_bx)
            "笃行" -> context.getString(R.string.bai_name_dx)
            "尽美" -> context.getString(R.string.bai_name_jm)
            "明德" -> context.getString(R.string.bai_name_md)
            else -> context.getString(R.string.bai_name_bx)
        }
        view.findViewById<TextView>(R.id.score_reason_text).text = scoreAdd.reason
    }
}