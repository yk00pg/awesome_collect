package webapp.AwesomeCollect.service.user;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.user.UserProgress;
import webapp.AwesomeCollect.repository.user.UserProgressRepository;
import webapp.AwesomeCollect.service.BonusAwesomeService;

/**
 * ユーザー進捗状況のサービスクラス。
 */
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
   * ユーザー進捗状況のエンティティを作成し、ユーザーIDと登録日を詰めてDBに登録する。
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
   * ユーザーIDを基にDBからユーザー進捗状況を取得し、アクション最終記録日がnullまたは今日でない場合は、
   * 累計記録日数、最終記録日、連続記録日数、ボーナスえらい！取得回数を更新し、ユーザー進捗状況を更新する。
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
   * アクション最終記録日がnullでなく、昨日の日付の場合は連続記録日数を1日増やし、そうでない場合は1日に戻す。<br>
   * 現在の連続記録日数が最大連続記録日数よりも大きい場合は、最大連続記録日数を更新する。
   *
   * @param yesterday　昨日の日付
   * @param lastActionDate  アクション最終記録日
   * @param currentStreak 現在の連続記録日数
   * @param userProgress  ユーザー進捗状況
   * @return  現在の連続記録日数
   */
  private int updateStreak(
      LocalDate yesterday, LocalDate lastActionDate, int currentStreak, UserProgress userProgress) {

    if (lastActionDate != null && lastActionDate.equals(yesterday)) {
      currentStreak += 1;
      userProgress.setCurrentStreak(currentStreak);
    } else {
      currentStreak = 1;
      userProgress.setCurrentStreak(currentStreak);
    }

    if (currentStreak > userProgress.getLongestStreak()) {
      userProgress.setLongestStreak(currentStreak);
    }

    return currentStreak;
  }
}
