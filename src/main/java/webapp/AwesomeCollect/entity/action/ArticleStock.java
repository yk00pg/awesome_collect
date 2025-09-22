package webapp.AwesomeCollect.entity.action;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 記事ストック情報を扱うオブジェクト。DBに存在するテーブルと連動する。
 */
@Getter
@Setter
@NoArgsConstructor
public class ArticleStock {

  private int id;
  private int userId;
  private String title;
  private String url;
  private String memo;
  private boolean finished;
  private LocalDateTime registeredAt;
  private LocalDateTime updatedAt;
}
