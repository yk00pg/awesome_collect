package webapp.AwesomeCollect.dto.dashboard;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import webapp.AwesomeCollect.entity.dashboard.AvgLearningTime;
import webapp.AwesomeCollect.entity.dashboard.TagLearningTime;
import webapp.AwesomeCollect.entity.dashboard.TotalLearningTime;

/**
 * 学習時間のデータオブジェクト。
 */
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
