package webapp.AwesomeCollect.entity.action;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Memo {

  private int id;
  private int userId;
  private String title;
  private String content;
  private LocalDateTime registeredAt;
  private LocalDateTime updatedAt;
}
