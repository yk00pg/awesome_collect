package webapp.AwesomeCollect.entity.action;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DailyDone {

  private int id;
  private int userId;
  private LocalDate date;
  private String content;
  private BigDecimal hours;
  private String memo;
  private LocalDateTime registeredAt;
  private LocalDateTime updatedAt;
}
