package webapp.AwesomeCollect.entity.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 曜日別平均学習時間を扱うオブジェクト。
 */
@Data
@AllArgsConstructor
public class AvgLearningTime {

  private int dayOfWeek;
  private int avgTime;
}
