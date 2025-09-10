package webapp.AwesomeCollect.entity.action;

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
  private int minutes;
  private String memo;
  private LocalDateTime registeredAt;
  private LocalDateTime updatedAt;
}
