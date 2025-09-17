package webapp.AwesomeCollect.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.entity.AvgLearningTime;
import webapp.AwesomeCollect.entity.TagLearningTime;
import webapp.AwesomeCollect.entity.TotalLearningTime;
import webapp.AwesomeCollect.mapper.LearningTimeMapper;

@Repository
public class LearningTimeRepository {

  private final LearningTimeMapper mapper;

  public LearningTimeRepository(LearningTimeMapper mapper) {
    this.mapper = mapper;
  }

  public int getTotalHours(int userId){
    return mapper.calculateTotalTime(userId);
  }

  public List<AvgLearningTime> getAvgDayOfWeekTime(int userId){
    return mapper.averageDayOfWeekTime(userId);
  }

  public List<TotalLearningTime> getDailyTotalHours(
      int userId, LocalDate fromDate, LocalDate toDate){

    return mapper.calculateDailyTotalTime(userId, fromDate, toDate);
  }

  public List<TotalLearningTime> getMonthlyTotalHours(
      int userId, LocalDate fromDate, LocalDate toDate){

    return mapper.calculateMonthlyTotalTime(userId, fromDate, toDate);
  }

  public List<TagLearningTime> getTotalHoursByTag(int userId){
    return mapper.calculateTotalTimeByTag(userId);
  }

}
