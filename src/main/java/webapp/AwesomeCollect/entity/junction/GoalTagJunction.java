package webapp.AwesomeCollect.entity.junction;

/**
 * 目標×タグ情報を扱うオブジェクト。DBに存在するテーブルと連動する。
 *
 * @param goalId  目標ID
 * @param tagId タグID
 */
public record GoalTagJunction(int goalId, int tagId) implements ActionTagJunction {

  @Override
  public int getActionId() {
    return goalId;
  }

  @Override
  public int getTagId() {
    return tagId;
  }
}
