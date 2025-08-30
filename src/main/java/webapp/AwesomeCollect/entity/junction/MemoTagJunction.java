package webapp.AwesomeCollect.entity.junction;

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
