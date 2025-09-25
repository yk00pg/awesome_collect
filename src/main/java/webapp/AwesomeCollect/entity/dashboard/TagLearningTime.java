package webapp.AwesomeCollect.entity.dashboard;

import lombok.Data;

/**
 * タグ別学習時間を扱うオブジェクト。
 */
@Data
public class TagLearningTime {

  private int tagId;
  private String tagName;
  private int totalTime;
}
