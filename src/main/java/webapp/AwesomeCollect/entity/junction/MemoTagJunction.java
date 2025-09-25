package webapp.AwesomeCollect.entity.junction;

/**
 * メモ×タグ情報を扱うオブジェクト。DBに存在するテーブルと連動する。
 *
 * @param memoId  メモID
 * @param tagId タグID
 */
public record MemoTagJunction(int memoId, int tagId) implements ActionTagJunction {

  @Override
  public int getActionId() {
    return memoId;
  }

  @Override
  public int getTagId() {
    return tagId;
  }
}
