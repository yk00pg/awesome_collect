package webapp.AwesomeCollect.entity.junction;

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
