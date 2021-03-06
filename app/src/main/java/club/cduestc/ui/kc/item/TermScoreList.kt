package club.cduestc.ui.kc.item

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import club.cduestc.R
import club.cduestc.ui.CollapseCardView

class TermScoreList(context: Context?,
                    title : String,
                    scoreList: List<ScoreLine>
) : LinearLayout(context) {

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.score_list, this)
        val flod = view.findViewById<TextView>(R.id.score_fold)
        flod.text = title
        flod.setOnClickListener(this::fldList)
        val list = findViewById<LinearLayout>(R.id.score_list)
        scoreList.forEach(list::addView)
    }

    private fun fldList(it : View){
        val list = findViewById<CollapseCardView>(R.id.score_card)
        list.toggle()
    }
}