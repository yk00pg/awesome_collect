package webapp.AwesomeCollect.entity.junction;

/**
 * できたこととタグの関係性情報を扱うオブジェクト。DBに存在するテーブルと連動する。
 *
 * @param doneId できたことID
 * @param tagId  タグID
 */
public record DoneTagJunction(int doneId, int tagId) implements ActionTagJunction {

  @Override
  public int getActionId() {
    return doneId;
  }

  @Override
  public int getTagId() {
    return tagId;
  }
}
