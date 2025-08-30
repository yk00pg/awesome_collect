package webapp.AwesomeCollect.entity.junction;

public record DoneTagJunction(int doneId, int tagId) implements ActionTagJunction{

  @Override
  public int getActionId() {
    return doneId;
  }

  @Override
  public int getTagId() {
    return tagId;
  }
}
