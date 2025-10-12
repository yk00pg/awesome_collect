package webapp.AwesomeCollect.provider.param;

import java.util.List;

public enum ActionTagJunctionName {
  DONE("done_tag", "done_id"),
  GOAL("goal_tag", "goal_id"),
  MEMO("memo_tag", "memo_id"),
  ARTICLE("article_tag", "article_id");

  private final String tableName;
  private final String columnName;

  ActionTagJunctionName(String tableName, String columnName) {
    this.tableName = tableName;
    this.columnName = columnName;
  }

  public JunctionDeleteParams toParams(List<Integer> actionIdList){
    return new JunctionDeleteParams(tableName, columnName, actionIdList);
  }
}
