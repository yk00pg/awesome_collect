package webapp.AwesomeCollect.dto.analysis;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import lombok.Data;

@Data
public class AvgLearningHoursDto {

  private DayOfWeek dayOfWeek;
  private BigDecimal avgHours;
}
