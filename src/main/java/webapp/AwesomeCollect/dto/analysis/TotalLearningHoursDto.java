package webapp.AwesomeCollect.dto.analysis;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TotalLearningHoursDto {

  private LocalDate date;
  private BigDecimal totalHours;
}
