package com.awesomecollect.service.dashboard;

import com.awesomecollect.common.util.DateTimeFormatUtil;
import com.awesomecollect.dto.dashboard.LearningDaysDto;
import com.awesomecollect.repository.dashboard.LearningDaysRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 学習日数のサービスクラス。
 */
@Service
public class LearningDaysService {

  private final LearningDaysRepository learningDaysRepository;

  public LearningDaysService(LearningDaysRepository learningDaysRepository) {
    this.learningDaysRepository = learningDaysRepository;
  }

  /**
   * 学習日数更新フラグがfalseで学習日数のキャッシュDTOがnullでない場合はそのままキャッシュDTOを返す。<br>
   * そうでない場合は、累計学習日数、連続学習日数、最終学習日を算出し、DTOに詰めて返す。
   *
   * @param userId ユーザーID
   * @param hasUpdatedLearnedDate 学習日数更新フラグ
   * @param cachedLearningDaysDto 学習日数のキャッシュDTO
   * @return 学習日数表示用データオブジェクト
   */
  @Transactional
  public LearningDaysDto prepareLearningDaysDto(
      int userId, Boolean hasUpdatedLearnedDate, LearningDaysDto cachedLearningDaysDto) {

    if(!hasUpdatedLearnedDate && cachedLearningDaysDto != null) {
      return cachedLearningDaysDto;
    } else {
      int totalDays = learningDaysRepository.getTotalDays(userId);
      List<LocalDate> learningDateList = learningDaysRepository.searchLearningDateList(userId);

      int streakDays = 0;
      String lastLearnedDate = "—";
      if (!learningDateList.isEmpty()) {
        LocalDate latestDate = learningDateList.getLast();
        for (int i = learningDateList.size() - 1; i >= 0; i--) {
          if (ChronoUnit.DAYS.between(
              learningDateList.get(i), latestDate.minusDays(streakDays)) == 0) {

            streakDays++;
          } else {
            break;
          }
        }
        lastLearnedDate = DateTimeFormatUtil.formatDate(latestDate);
      }

      return new LearningDaysDto(totalDays, streakDays, lastLearnedDate);
    }
  }
}
