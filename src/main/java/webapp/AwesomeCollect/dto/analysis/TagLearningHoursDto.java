package webapp.AwesomeCollect.dto.analysis;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TagLearningHoursDto {

  private int tagId;
  private String tagName;
  private BigDecimal totalHours;
}
