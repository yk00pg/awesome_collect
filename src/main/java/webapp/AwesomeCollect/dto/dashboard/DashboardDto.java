package webapp.AwesomeCollect.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ダッシュボード表示用統括データオブジェクト。<br>
 * えらい！ポイント、学習時間、学習日数のデータオブジェクトを内包する。
 */
@Data
@AllArgsConstructor
public class DashboardDto {

  private AwesomePointDto awesomePointDto;
  private LearningDaysDto learningDaysDto;
  private LearningTimeDto learningTimeDto;
}
