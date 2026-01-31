package webapp.AwesomeCollect.service.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.provider.param.ActionTagJunctionName;
import webapp.AwesomeCollect.repository.BonusAwesomeRepository;
import webapp.AwesomeCollect.repository.TagRepository;
import webapp.AwesomeCollect.repository.action.ArticleStockRepository;
import webapp.AwesomeCollect.repository.action.DailyDoneRepository;
import webapp.AwesomeCollect.repository.action.DailyTodoRepository;
import webapp.AwesomeCollect.repository.action.GoalRepository;
import webapp.AwesomeCollect.repository.action.MemoRepository;
import webapp.AwesomeCollect.repository.junction.ArticleTagJunctionRepository;
import webapp.AwesomeCollect.repository.junction.DoneTagJunctionRepository;
import webapp.AwesomeCollect.repository.junction.GoalTagJunctionRepository;
import webapp.AwesomeCollect.repository.junction.MemoTagJunctionRepository;
import webapp.AwesomeCollect.repository.user.UserInfoRepository;
import webapp.AwesomeCollect.repository.user.UserProgressRepository;

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

  @Transactional
  public void deleteUserData(int userId){

    dailyTodoRepository.deleteALlTodoByUserId(userId);

    List<Integer> doneIdList = dailyDoneRepository.searchIdByUserId(userId);
    if(doneIdList != null && !doneIdList.isEmpty()){
      doneTagJunctionRepository.deleteAllRelationsByActionIdList(
          ActionTagJunctionName.DONE.toParams(doneIdList));
      dailyDoneRepository.deleteAllDoneByUserId(userId);
    }

    List<Integer> goalIdList = goalRepository.searchIdByUserId(userId);
    if(goalIdList != null && !goalIdList.isEmpty()){
      goalTagJunctionRepository.deleteAllRelationsByActionIdList(
          ActionTagJunctionName.GOAL.toParams(goalIdList));
      goalRepository.deleteAllGoalByUserId(userId);
    }

    List<Integer> memoIdList = memoRepository.searchIdByUserId(userId);
    if(memoIdList != null && !memoIdList.isEmpty()){
      memoTagJunctionRepository.deleteAllRelationsByActionIdList(
          ActionTagJunctionName.MEMO.toParams(memoIdList));
      memoRepository.deleteAllMemoByUserId(userId);
    }

    List<Integer> articleIdList = articleStockRepository.searchIdByUserId(userId);
    if(articleIdList != null && !articleIdList.isEmpty()){
      articleTagJunctionRepository.deleteAllRelationsByActionIdList(
          ActionTagJunctionName.ARTICLE.toParams(articleIdList));
      articleStockRepository.deleteAllArticleStockByUserId(userId);
    }

    tagRepository.deleteAllTagByUserId(userId);
    bonusAwesomeRepository.deleteAllBonusAwesomeByUserId(userId);
    userProgressRepository.deleteUserProgressByUserId(userId);
    userInfoRepository.deleteUserInfoById(userId);
  }
}
