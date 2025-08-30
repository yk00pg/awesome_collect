package webapp.AwesomeCollect.entity.junction;

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
