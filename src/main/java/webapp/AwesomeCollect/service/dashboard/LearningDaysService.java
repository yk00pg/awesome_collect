package webapp.AwesomeCollect.service.dashboard;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.common.SessionManager;
import webapp.AwesomeCollect.common.util.DateTimeFormatUtil;
import webapp.AwesomeCollect.dto.dashboard.LearningDaysDto;
import webapp.AwesomeCollect.repository.dashboard.LearningDaysRepository;

/**
 * 学習日数をまとめるサービスクラス。
 */
@Service
public class LearningDaysService {

  private final LearningDaysRepository learningDaysRepository;
  private final SessionManager sessionManager;

  public LearningDaysService(
      LearningDaysRepository learningDaysRepository, SessionManager sessionManager){

    this.learningDaysRepository = learningDaysRepository;
    this.sessionManager = sessionManager;
  }

  /**
   * セッション情報を確認し、セッション情報がnullまたは学習日数の更新有りまたはDTOがnullの場合は
   * 累計学習日数、連続学習日数、最終学習日を算出し、DTOに詰めてセッション情報を更新する。
   *
   * @param userId  ユーザーID
   * @return  学習日数のデータオブジェクト
   */
  @Transactional
  public LearningDaysDto prepareLearningDaysDto(int userId){
    Boolean hasUpdatedLearnedDate = sessionManager.hasUpdatedLearningDays();
    LearningDaysDto learningDaysDto = sessionManager.getCachedLearningDaysDto();

    if(hasUpdatedLearnedDate == null || hasUpdatedLearnedDate || learningDaysDto == null){
      int totalDays = learningDaysRepository.getTotalDays(userId);
      List<LocalDate> learningDateList = learningDaysRepository.searchLearningDateList(userId);

      int streakDays = 0;
      String lastLearnedDate = "—";
      if(!learningDateList.isEmpty()){
        LocalDate latestDate = learningDateList.getLast();
        for(int i = learningDateList.size() -1; i >= 0; i--) {
          if (ChronoUnit.DAYS.between(learningDateList.get(i), latestDate.minusDays(streakDays)) == 0) {
            streakDays++;
          } else {
            break;
          }
        }
        lastLearnedDate = DateTimeFormatUtil.formatDate(latestDate);
      }

      learningDaysDto = new LearningDaysDto(totalDays, streakDays, lastLearnedDate);
      sessionManager.setLearningDaysDto(learningDaysDto);
      sessionManager.setHasUpdatedLearningDays(false);
    }

    return learningDaysDto;
  }
}
