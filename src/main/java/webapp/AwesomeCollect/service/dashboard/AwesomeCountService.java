package webapp.AwesomeCollect.service.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.common.util.SessionManager;
import webapp.AwesomeCollect.dto.dashboard.AwesomePointDto;
import webapp.AwesomeCollect.repository.dashboard.AwesomeCountRepository;
import webapp.AwesomeCollect.service.BonusAwesomeService;

/**
 * えらい！ポイントのサービスクラス。
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
      BonusAwesomeService bonusAwesomeService) {

    this.sessionManager = sessionManager;
    this.awesomeCountRepository = awesomeCountRepository;
    this.bonusAwesomeService = bonusAwesomeService;
  }

  /**
   * セッション情報を確認し、レコード数更新情報がnullまたは更新有りあるいはDTOがnullの場合は、
   * 累計えらい！ポイントを算出し、分割したえらい！ポイントリストを作成してDTOに詰め、
   * セッション情報を更新する。
   *
   * @param userId ユーザーID
   * @return えらい！ポイント表示用データオブジェクト
   */
  @Transactional
  public AwesomePointDto prepareAwesomePointDto(int userId) {
    AwesomePointDto awesomePointDto = sessionManager.getCachedAwesomePointDto();
    Boolean hasNewRecord = sessionManager.hasUpdatedRecordCount();

    if (hasNewRecord == null || hasNewRecord || awesomePointDto == null) {
      int totalAwesomePoint = calculateTotalAwesome(userId);
      List<Integer> splitTotalAwesomeList = createSplitTotalAwesomeList(totalAwesomePoint);

      awesomePointDto = new AwesomePointDto(totalAwesomePoint, splitTotalAwesomeList);
      sessionManager.setAwesomePointDto(awesomePointDto);
      sessionManager.setHasUpdatedRecordCount(false);
    }
    return awesomePointDto;
  }

  /**
   * 各アクションのレコード数と配点を掛け合わせたポイントと、ボーナスえらい！ポイントを
   * 足し合わせて累計えらい！ポイントを算出する。
   *
   * @param userId ユーザーID
   * @return 累計えらい！ポイント
   */
  private int calculateTotalAwesome(int userId) {
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

    return totalNormalPoint + totalBonusPoint;
  }

  /**
   * 累計えらい！ポイントを100ごとに分割してリスト化し、先頭に余りを追加する。
   *
   * @param totalAwesome 累計えらい！ポイント
   * @return 分割したえらい！ポイントリスト
   */
  private List<Integer> createSplitTotalAwesomeList(int totalAwesome) {
    List<Integer> splitTotalAwesomeList = new ArrayList<>();
    int remainingNum = totalAwesome % 100;
    int hundreds = totalAwesome / 100;

    if (remainingNum != 0) {
      splitTotalAwesomeList.add(remainingNum);
    }
    IntStream.range(0, hundreds)
        .mapToObj(i -> 100).forEach(splitTotalAwesomeList :: add);

    return splitTotalAwesomeList;
  }
}
