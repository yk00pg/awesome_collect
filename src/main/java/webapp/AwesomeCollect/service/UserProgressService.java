package webapp.AwesomeCollect.service;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.UserProgress;
import webapp.AwesomeCollect.repository.UserProgressRepository;

@Service
public class UserProgressService {

  private final UserProgressRepository userProgressRepository;
  private final BonusAwesomeService bonusAwesomeService;

  public UserProgressService(
      UserProgressRepository userProgressRepository, BonusAwesomeService bonusAwesomeService){

    this.userProgressRepository = userProgressRepository;
    this.bonusAwesomeService = bonusAwesomeService;
  }

  /**
   * 進捗管理データ（ユーザーID、登録日）を作成し、DBに登録する。
   *
   * @param userId  ユーザーID
   */
  public void createUserProgress(int userId){
    UserProgress userProgress = new UserProgress();
    userProgress.setUserId(userId);
    userProgress.setRegisteredDate(LocalDate.now());

    userProgressRepository.registerUserProgress(userProgress);
  }

  /**
   * 進捗管理データを更新する。<br>
   * 既存データの登録状況に応じてアクション累計記録日数、アクション最終記録日、
   * 現在の連続記録日数、最大連続記録日数、ボーナスえらい！ポイントの獲得状況を更新する。
   *
   * @param userId  ユーザーID
   */
  @Transactional
  public void updateUserProgress(int userId) {
    UserProgress userProgress = userProgressRepository.findUserProgressByUserId(userId);

    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    LocalDate lastActionDate = userProgress.getLastActionDate();
    int currentStreak = userProgress.getCurrentStreak();

    // アクション最終記録日がnullまたは今日でない場合
    if (lastActionDate == null || !lastActionDate.equals(today)) {
      userProgress.setTotalActionDays(userProgress.getTotalActionDays() + 1);
      userProgress.setLastActionDate(today);

      currentStreak = updateStreak(yesterday, lastActionDate, currentStreak, userProgress);

      int newStreakBonusCount =
          bonusAwesomeService.registerBonusAwesome(userId, currentStreak, today);

      int currentStreakBonusCount = userProgress.getStreakBonusCount();
      userProgress.setStreakBonusCount(currentStreakBonusCount + newStreakBonusCount);

      userProgressRepository.updateUserProgress(userProgress);
    }
  }

  /**
   * 既存データの登録状況に応じて、現在の連続記録日数、最大連続記録日数を更新し、現在の連続記録日数を返す。
   *
   * @param yesterday　昨日の日付
   * @param lastActionDate  アクション最終記録日
   * @param currentStreak 現在の連続記録日数
   * @param userProgress  進捗管理データ
   * @return  現在の連続記録日数
   */
  private int updateStreak(
      LocalDate yesterday, LocalDate lastActionDate, int currentStreak, UserProgress userProgress) {

    // アクション最終記録日がnullではなく、昨日の日付の場合
    if (lastActionDate != null && lastActionDate.equals(yesterday)) {
      currentStreak += 1;
      userProgress.setCurrentStreak(currentStreak);
    } else {
      currentStreak = 1;
      userProgress.setCurrentStreak(currentStreak);
    }

    // 現在の連続記録日数が最大連続記録日数よりも大きい場合
    if (currentStreak > userProgress.getLongestStreak()) {
      userProgress.setLongestStreak(currentStreak);
    }

    return currentStreak;
  }
}
