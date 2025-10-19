package webapp.AwesomeCollect.provider.param;

import java.util.List;

/**
 * 中間テーブルから関係性を削除する際の引数を扱うレコードクラス。
 *
 * @param tableName テーブル名
 * @param columnName  カラム名
 * @param actionIdList  学習アクションIDリスト
 */
public record JunctionDeleteParams(
    String tableName, String columnName, List<Integer> actionIdList) {}
