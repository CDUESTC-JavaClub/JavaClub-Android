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
                 private val onClick : Consumer<Int>,
                 private val selected : Boolean) :
    LinearLayout(context) {

    var select = false
    private var cardBackgroundColor : ColorStateList? = null
    var textColor : ColorStateList? = null

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.week_select, this)
        view.findViewById<TextView>(R.id.text).text = "第${index}周"
        this.setOnClickListener { if(!select) select() }
        select = selected
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val card = this.findViewById<CardView>(R.id.card)
        val text = this.findViewById<TextView>(R.id.text)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(this.cardBackgroundColor == null) this.cardBackgroundColor = card.cardBackgroundColor
        if(this.textColor == null) this.textColor = text.textColors
        if(selected){
            card.setCardBackgroundColor(Color.parseColor("#4CAF50"))
            text.setTextColor(Color.parseColor("#FFFFFF"))
        }
    }

    fun select(){
        val card = this.findViewById<CardView>(R.id.card)
        val text = this.findViewById<TextView>(R.id.text)
        select = if(!select){
            child.forEach { view ->
                view as WeekSelect
                if(view != this && view.select) view.select()
            }
            card.setCardBackgroundColor(Color.parseColor("#4CAF50"))
            text.setTextColor(Color.parseColor("#FFFFFF"))
            onClick.accept(index)
            true
        }else{
            card.setCardBackgroundColor(this.cardBackgroundColor)
            text.setTextColor(textColor)
            false
        }
    }
}