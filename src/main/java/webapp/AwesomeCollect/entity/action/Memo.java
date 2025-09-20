package webapp.AwesomeCollect.entity.action;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * メモ情報を扱うオブジェクト。DBテーブルのカラムと連動する。
 */
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
