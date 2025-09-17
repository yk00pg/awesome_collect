package webapp.AwesomeCollect.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import webapp.AwesomeCollect.entity.AvgLearningTime;
import webapp.AwesomeCollect.entity.TagLearningTime;
import webapp.AwesomeCollect.entity.TotalLearningTime;

@Data
@AllArgsConstructor
public class LearningTimeDto {

  private int totalHours;
  private int totalMinutes;
  private List<TotalLearningTime> dailyTotalTimeList;
  private List<AvgLearningTime> dayOfWeekAvgTimeList;
  private List<TotalLearningTime> monthlyTotalTimeList;
  private List<TagLearningTime> topTenTagTotalTimeList;
  private List<TagLearningTime> tagTotalTimeList;

}
