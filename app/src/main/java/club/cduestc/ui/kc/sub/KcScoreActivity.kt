package club.cduestc.ui.kc.sub

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import club.cduestc.R
import club.cduestc.ui.kc.item.ScoreLine
import club.cduestc.ui.kc.item.TermScoreList
import club.cduestc.util.NetManager
import club.cduestc.util.UserManager
import club.jw.score.ScoreList
import kotlin.streams.toList

class KcScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kc_score)

        window.navigationBarColor = Color.TRANSPARENT
        this.initScore()
    }

    private fun initScore(){
        NetManager.createTask{
            try {
                val scoreList = UserManager.kcAccount.score
                runOnUiThread {
                    listScore(scoreList)
                    findViewById<View>(R.id.score_loading).visibility = View.GONE
                    findViewById<View>(R.id.score_menu).visibility = View.VISIBLE
                }
            }catch (e : Exception){
                this.runOnUiThread {
                    e.printStackTrace()
                    Toast.makeText(this, "未知错误，无法获取成绩信息！", Toast.LENGTH_SHORT).show()
                    this.finish()
                }
            }
        }
    }

    private fun listScore(scoreList: ScoreList){
        val menu = findViewById<LinearLayout>(R.id.score_menu)
        scoreList.terms.forEach {
            if(scoreList.getScore(it, 2).isNotEmpty()){
                val term2 = scoreList.getScore(it, 2).stream().map { inner ->
                    ScoreLine(this, "学分 "+inner.credits+"（绩点"+inner.points+"）", inner.name, inner.finalScore.toString())
                }.toList()
                val list2 = TermScoreList(this, it+"学年 第2学期", term2)
                menu.addView(list2)
            }

            val term1 = scoreList.getScore(it, 1).stream().map { inner ->
                ScoreLine(this, "学分 "+inner.credits+"（绩点"+inner.points+"）", inner.name, inner.finalScore.toString())
            }.toList()
            val list = TermScoreList(this, it+"学年 第1学期", term1)
            menu.addView(list)
        }
    }
}