package webapp.AwesomeCollect.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvgLearningTime {

  private int dayOfWeek;
  private int avgTime;
}
