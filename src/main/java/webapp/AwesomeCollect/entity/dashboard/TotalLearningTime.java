package webapp.AwesomeCollect.entity.dashboard;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TotalLearningTime {
  private LocalDate date;
  private int totalTime;

}
