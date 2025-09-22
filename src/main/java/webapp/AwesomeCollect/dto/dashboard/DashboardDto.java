package webapp.AwesomeCollect.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ダッシュボードのデータオブジェクト。
 */
@Data
@AllArgsConstructor
public class DashboardDto {

  private int totalAwesome;
  private LearningTimeDto learningTimeDto;
  private LearningDaysDto learningDaysDto;
}
