package club.byjh.entity.account;

import club.byjh.entity.activity.SignedActivity;
import club.byjh.entity.score.ScoreAdd;
import club.byjh.entity.score.ScoreData;
import club.byjh.exception.ActivityOprException;
import club.byjh.exception.LoginException;

import java.util.List;

/**
 * 可操作账户接口，包含所有的常规操作
 *
 * @author Ketuer
 * @since 1.0
 */
public interface OperableAccount {
    void signActivity(int activityId) throws ActivityOprException;
    void cancelActivity(int activityId) throws ActivityOprException;
    List<SignedActivity> getActivities() throws ActivityOprException;
    ScoreData getScore() throws LoginException;
    List<ScoreAdd> getScoreAddList() throws LoginException;
}
