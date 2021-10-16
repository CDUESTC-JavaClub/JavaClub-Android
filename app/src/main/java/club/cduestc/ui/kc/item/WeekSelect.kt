package club.cduestc.ui.kc.item

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import club.cduestc.R
import java.util.function.Consumer

class WeekSelect(context: Context,
                 private val index : Int,
                 private val child : Sequence<View>,
                 private val onClick : Consumer<Int>) :
    LinearLayout(context) {

    var select = false
    var cardBackgroundColor : ColorStateList? = null

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.week_select, this)
        view.findViewById<TextView>(R.id.text).text = "第${index}周"
        this.setOnClickListener {
            select()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        this.cardBackgroundColor = this.findViewById<CardView>(R.id.card).cardBackgroundColor
    }

    private fun select(){
        val card = this.findViewById<CardView>(R.id.card)
        child.forEach { view ->
            view as WeekSelect
            if(view != this && view.select) view.select()
        }
        select = if(!select){
            card.setCardBackgroundColor(Color.parseColor("#4CAF50"))
            onClick.accept(index)
            true
        }else{
            card.setCardBackgroundColor(this.cardBackgroundColor)
            false
        }
    }
}