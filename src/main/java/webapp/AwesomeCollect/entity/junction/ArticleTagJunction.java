package webapp.AwesomeCollect.entity.junction;

/**
 * 記事ストック×タグを扱うオブジェクト。DBに存在するテーブルと連動する。
 *
 * @param articleId 記事ストックID
 * @param tagId タグID
 */
public record ArticleTagJunction(int articleId, int tagId) implements ActionTagJunction{

  @Override
  public int getActionId() {
    return articleId;
  }

  @Override
  public int getTagId() {
    return tagId;
  }
}
