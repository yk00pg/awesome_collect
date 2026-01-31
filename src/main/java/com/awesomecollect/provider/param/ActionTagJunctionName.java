package com.awesomecollect.provider.param;

import java.util.List;

/**
 * 学習アクションごとの中間テーブル情報を扱うenumクラス。
 */
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

  /**
   * テーブル名、カラム名、学習アクションIDリストを設定して関係性を削除する際の引数オブジェクトを作成する。
   *
   * @param actionIdList  学習アクションIDリスト
   * @return  関係性を削除する際の引数オブジェクト
   */
  public JunctionDeleteParams toParams(List<Integer> actionIdList){
    return new JunctionDeleteParams(tableName, columnName, actionIdList);
  }
}
