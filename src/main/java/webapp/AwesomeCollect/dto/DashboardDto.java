package webapp.AwesomeCollect.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import webapp.AwesomeCollect.dto.analysis.AvgLearningHoursDto;
import webapp.AwesomeCollect.dto.analysis.TagLearningHoursDto;
import webapp.AwesomeCollect.dto.analysis.TotalLearningHoursDto;

@Data
public class DashboardDto {

  private Integer totalAwesome;
  private BigDecimal totalHours;
  private List<TotalLearningHoursDto> dailyHoursList;
  private List<AvgLearningHoursDto> dayOfWeekHoursList;
  private List<TotalLearningHoursDto> monthlyHoursList;
  private List<TagLearningHoursDto> totalHoursByTag;
}
