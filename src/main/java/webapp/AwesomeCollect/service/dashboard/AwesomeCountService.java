package webapp.AwesomeCollect.service.dashboard;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.common.SessionManager;
import webapp.AwesomeCollect.repository.dashboard.AwesomeCountRepository;
import webapp.AwesomeCollect.service.BonusAwesomeService;

/**
 * えらいポイントのサービスクラス。
 */
@Service
public class AwesomeCountService {

  private final SessionManager sessionManager;
  private final AwesomeCountRepository awesomeCountRepository;
  private final BonusAwesomeService bonusAwesomeService;

  // 通常のえらい！ポイント
  private static final int TODO = 1;
  private static final int DONE = 3;
  private static final int GOAL = 5;
  private static final int ACHIEVED = 10;
  private static final int MEMO = 5;
  private static final int ARTICLE_STOCK = 3;
  private static final int FINISHED = 5;

  public AwesomeCountService(
      SessionManager sessionManager, AwesomeCountRepository awesomeCountRepository,
      BonusAwesomeService bonusAwesomeService){

    this.sessionManager = sessionManager;
    this.awesomeCountRepository = awesomeCountRepository;
    this.bonusAwesomeService = bonusAwesomeService;
  }

  /**
   * セッション情報を確認し、レコード数更新情報がnullまたは更新有りの場合は、
   * 各アクションのレコード数と配点を掛け合わせたポイントと、ボーナスえらいポイントを足し合わせて
   * 累計えらいポイントを算出し、セッション情報を更新する。
   *
   * @param userId  ユーザーID
   * @return  累計えらいポイント
   */
  @Transactional
  public int calculateTotalAwesome(int userId){
    Integer totalAwesomePoint = sessionManager.getCachedAwesomePoint();
    Boolean hasNewRecord = sessionManager.hasUpdatedRecordCount();

    if(hasNewRecord == null || hasNewRecord){
      int todoPoint = awesomeCountRepository.countDailyTodoRecord(userId) * TODO;
      int donePoint = awesomeCountRepository.countDailyDoneRecord(userId) * DONE;
      int goalPoint = awesomeCountRepository.countGoalRecord(userId) * GOAL;
      int achievedPoint =
          awesomeCountRepository.countAchievedGoalRecord(userId) * ACHIEVED;
      int memoPoint = awesomeCountRepository.countMemoRecord(userId) * MEMO;
      int articleStockPoint =
          awesomeCountRepository.countArticleStockRecord(userId) * ARTICLE_STOCK;
      int finishedPoint =
          awesomeCountRepository.countFinishedArticleRecord(userId) * FINISHED;

      int totalNormalPoint =
          todoPoint + donePoint + goalPoint + achievedPoint
              + memoPoint + articleStockPoint + finishedPoint;

      int totalBonusPoint = bonusAwesomeService.calculateTotalBonusCount(userId);

      totalAwesomePoint = totalNormalPoint + totalBonusPoint;

      sessionManager.setAwesomePoint(totalAwesomePoint);
      sessionManager.setHasUpdatedRecordCount(false);
    }
    return totalAwesomePoint;
  }
}
