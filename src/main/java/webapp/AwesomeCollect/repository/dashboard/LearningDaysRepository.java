package webapp.AwesomeCollect.repository.dashboard;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.mapper.dashboard.LearningDaysMapper;

/**
 * 学習日数のリポジトリクラス。
 */
@Repository
public class LearningDaysRepository {

  private final LearningDaysMapper mapper;

  public LearningDaysRepository(LearningDaysMapper mapper){
    this.mapper = mapper;
  }

  public int getTotalDays(int userId){
    return mapper.countTotalLearningDate(userId);
  }

  public List<LocalDate> searchLearningDateList(int userId){
    return mapper.selectDistinctLearningDate(userId);
  }
}
