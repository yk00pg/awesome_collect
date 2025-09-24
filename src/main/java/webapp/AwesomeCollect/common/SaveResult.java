package webapp.AwesomeCollect.common;

/**
 * アクションIDとステータス更新情報を扱うオブジェクト。<br>
 * データの保存方法（新規登録・更新）と内容を判別する。
 *
 * @param id  アクションID
 * @param isUpdatedStatus ステータスが更新されたか
 */
public record SaveResult(int id, boolean isUpdatedStatus) {}