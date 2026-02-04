package com.awesomecollect.service.dashboard;

import com.awesomecollect.dto.dashboard.AwesomePointDto;
import com.awesomecollect.repository.dashboard.AwesomeCountRepository;
import com.awesomecollect.service.BonusAwesomeService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * えらい！ポイントのサービスクラス。
 */
@Service
public class AwesomeCountService {

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
      AwesomeCountRepository awesomeCountRepository,
      BonusAwesomeService bonusAwesomeService) {

    this.awesomeCountRepository = awesomeCountRepository;
    this.bonusAwesomeService = bonusAwesomeService;
  }

  /**
   * ダッシュボード表示用えらいポイントデータを用意する。<br>
   * えらいポイントのキャッシュデータ保持フラグがtrueでえらいポイントのキャッシュDTOがnullでない場合は、
   * そのままキャッシュDTOを返す。<br>
   * そうでない場合は、累計えらいポイントを算出し、分割したえらいポイントリストを作成してDTOに詰めて返す。
   *
   * @param userId ユーザーID
   * @param hasCachedAwesomePoints えらいポイントのキャッシュデータ保持フラグ
   * @param cachedAwesomePointDto えらいポイントのキャッシュDTO
   * @return えらいポイント表示用データオブジェクト
   */
  @Transactional(readOnly = true)
  public AwesomePointDto prepareAwesomePointDto(
      int userId, boolean hasCachedAwesomePoints, AwesomePointDto cachedAwesomePointDto) {

    if(hasCachedAwesomePoints && cachedAwesomePointDto != null) {
      return cachedAwesomePointDto;
    } else {
      int totalAwesomePoint = calculateTotalAwesome(userId);
      List<Integer> splitTotalAwesomeList = createSplitTotalAwesomeList(totalAwesomePoint);

      return new AwesomePointDto(totalAwesomePoint, splitTotalAwesomeList);
    }
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
