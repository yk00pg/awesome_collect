package webapp.AwesomeCollect;

/**
 * データの保存方法（新規登録・更新）や内容を判別するためのオブジェクト。
 *
 * @param id  アクションID
 * @param isUpdatedStatus ステータスが更新されたか
 */
public record SaveResult(int id, boolean isUpdatedStatus) {}