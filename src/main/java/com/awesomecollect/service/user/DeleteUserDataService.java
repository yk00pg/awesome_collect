package com.awesomecollect.service.user;

import com.awesomecollect.provider.param.ActionTagJunctionName;
import com.awesomecollect.repository.BonusAwesomeRepository;
import com.awesomecollect.repository.TagRepository;
import com.awesomecollect.repository.action.ArticleStockRepository;
import com.awesomecollect.repository.action.DailyDoneRepository;
import com.awesomecollect.repository.action.DailyTodoRepository;
import com.awesomecollect.repository.action.GoalRepository;
import com.awesomecollect.repository.action.MemoRepository;
import com.awesomecollect.repository.junction.ArticleTagJunctionRepository;
import com.awesomecollect.repository.junction.DoneTagJunctionRepository;
import com.awesomecollect.repository.junction.GoalTagJunctionRepository;
import com.awesomecollect.repository.junction.MemoTagJunctionRepository;
import com.awesomecollect.repository.user.UserInfoRepository;
import com.awesomecollect.repository.user.UserProgressRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ユーザーデータの削除に関するサービスクラス。
 */
@Service
@RequiredArgsConstructor
public class DeleteUserDataService {

  private final DailyTodoRepository dailyTodoRepository;
  private final DailyDoneRepository dailyDoneRepository;
  private final DoneTagJunctionRepository doneTagJunctionRepository;
  private final GoalRepository goalRepository;
  private final GoalTagJunctionRepository goalTagJunctionRepository;
  private final MemoRepository memoRepository;
  private final MemoTagJunctionRepository memoTagJunctionRepository;
  private final ArticleStockRepository articleStockRepository;
  private final ArticleTagJunctionRepository articleTagJunctionRepository;
  private final TagRepository tagRepository;
  private final BonusAwesomeRepository bonusAwesomeRepository;
  private final UserProgressRepository userProgressRepository;
  private final UserInfoRepository userInfoRepository;

  // ユーザーIDを基にDBのデータをすべて削除する。
  @Transactional
  public void deleteUserData(int userId){

    dailyTodoRepository.deleteALlTodoByUserId(userId);
    deleteAllDoneAndTagRelations(userId);
    deleteAllGoalAndTagRelations(userId);
    deleteAllMemoAndTagRelations(userId);
    deleteArticleStockAndTagRelations(userId);

    tagRepository.deleteAllTagByUserId(userId);
    bonusAwesomeRepository.deleteAllBonusAwesomeByUserId(userId);
    userProgressRepository.deleteUserProgressByUserId(userId);
    userInfoRepository.deleteUserInfoById(userId);
  }

  // ユーザーIDを基にできたこと、紐付いたタグの関係情報をすべて削除する。
  private void deleteAllDoneAndTagRelations(int userId) {
    List<Integer> doneIdList = dailyDoneRepository.searchIdByUserId(userId);
    if(doneIdList != null && !doneIdList.isEmpty()){
      doneTagJunctionRepository.deleteAllRelationsByActionIdList(
          ActionTagJunctionName.DONE.toParams(doneIdList));
      dailyDoneRepository.deleteAllDoneByUserId(userId);
    }
  }

  // ユーザーIDを基に目標、紐付いたタグの関係情報をすべて削除する。
  private void deleteAllGoalAndTagRelations(int userId) {
    List<Integer> goalIdList = goalRepository.searchIdByUserId(userId);
    if(goalIdList != null && !goalIdList.isEmpty()){
      goalTagJunctionRepository.deleteAllRelationsByActionIdList(
          ActionTagJunctionName.GOAL.toParams(goalIdList));
      goalRepository.deleteAllGoalByUserId(userId);
    }
  }

  // ユーザーIDを基にメモ、紐付いたタグの関係情報をすべて削除する。
  private void deleteAllMemoAndTagRelations(int userId) {
    List<Integer> memoIdList = memoRepository.searchIdByUserId(userId);
    if(memoIdList != null && !memoIdList.isEmpty()){
      memoTagJunctionRepository.deleteAllRelationsByActionIdList(
          ActionTagJunctionName.MEMO.toParams(memoIdList));
      memoRepository.deleteAllMemoByUserId(userId);
    }
  }

  // ユーザーIDを基に記事ストック、紐付いたタグの関係情報をすべて削除する。
  private void deleteArticleStockAndTagRelations(int userId) {
    List<Integer> articleIdList = articleStockRepository.searchIdByUserId(userId);
    if(articleIdList != null && !articleIdList.isEmpty()){
      articleTagJunctionRepository.deleteAllRelationsByActionIdList(
          ActionTagJunctionName.ARTICLE.toParams(articleIdList));
      articleStockRepository.deleteAllArticleStockByUserId(userId);
    }
  }
}
