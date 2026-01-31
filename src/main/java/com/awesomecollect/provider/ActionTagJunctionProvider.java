package com.awesomecollect.provider;

import java.util.List;
import java.util.stream.Collectors;
import com.awesomecollect.provider.param.JunctionDeleteParams;

/**
 * アクションとタグの関係性のプロバイダクラス。
 */
public class ActionTagJunctionProvider {

  // Mapperに渡された引数オブジェクトから取得したリストを文字列結合してSQL文を作成する。
  public String deleteRelationsByActionIdList(JunctionDeleteParams params){
    List<Integer> actionIdList = params.actionIdList();

    // 構文エラー防止
    if(actionIdList == null || actionIdList.isEmpty()){
      return "SELECT 1";
    }

    String inClause = actionIdList.stream()
        .map(String::valueOf)
        .collect(Collectors.joining(","));

    return "DELETE FROM " + params.tableName()
        + " WHERE " + params.columnName() + " IN (" + inClause + ")";
  }
}
