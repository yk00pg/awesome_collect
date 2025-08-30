package webapp.AwesomeCollect.entity;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BonusAwesome {

  private int id;
  private int userId;
  private int awesomePoint;
  private String reason;
  private LocalDate collectedDate;
}
