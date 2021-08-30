package club.cduestc.util

import club.jw.auth.KcAccount
import club.jw.exception.AuthorizationException
import club.jw.score.ScoreList

object KcManager {
    fun getScore(password : String) : ScoreList{
        val account = KcAccount.create(UserManager.getBindId(), password)
        account.login()
        val scoreList = account.score
        account.logout()
        return scoreList
    }
}