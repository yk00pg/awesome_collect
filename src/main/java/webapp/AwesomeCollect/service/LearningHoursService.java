package webapp.AwesomeCollect.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.dto.analysis.TagLearningHoursDto;
import webapp.AwesomeCollect.dto.analysis.TotalLearningHoursDto;
import webapp.AwesomeCollect.mapper.LearningHoursMapper;

@Service
public class LearningHoursService {

  private final LearningHoursMapper mapper;

  public LearningHoursService(LearningHoursMapper mapper) {
    this.mapper = mapper;
  }

  public BigDecimal getTotalHours(int userId){
    return mapper.calculateTotalHours(userId);
  }

  public List<TotalLearningHoursDto> getDailyTotalHours(
      int userId, LocalDate fromDate, LocalDate toDate){

    return mapper.calculateDailyTotalHours(userId, fromDate, toDate);
  }

  public List<TotalLearningHoursDto> getMonthlyTotalHours(
      int userId, LocalDate fromDate, LocalDate toDate){

    return mapper.calculateMonthlyTotalHours(userId, fromDate, toDate);
  }

  public List<TagLearningHoursDto> getTotalHoursByTag(int userId){
    return mapper.calculateTotalHoursByTag(userId);
  }
}
