package webapp.AwesomeCollect.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LearningDaysDto {

  private int totalDays;
  private int streakDays;
  private String lastLearnedDate;
}
