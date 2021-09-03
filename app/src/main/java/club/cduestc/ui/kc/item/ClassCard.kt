package club.cduestc.ui.kc.item

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import club.cduestc.R

class ClassCard(
    context: Context?,
    private val name: String,
    private val color: String?
) : LinearLayout(context) {

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.class_card, this)
        val name = view.findViewById<TextView>(R.id.card_name)
        name.text = this.name
        val body = view.findViewById<CardView>(R.id.card_body)
        if(color != null) body.backgroundTintList = ColorStateList.valueOf(Color.parseColor(this.color))
    }
}