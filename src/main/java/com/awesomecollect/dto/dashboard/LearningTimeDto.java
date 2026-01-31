package com.awesomecollect.dto.dashboard;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.awesomecollect.entity.dashboard.AvgLearningTime;
import com.awesomecollect.entity.dashboard.TagLearningTime;
import com.awesomecollect.entity.dashboard.TotalLearningTime;

/**
 * 学習時間表示用データオブジェクト。
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
