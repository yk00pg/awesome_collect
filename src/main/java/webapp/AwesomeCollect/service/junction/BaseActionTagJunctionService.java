package webapp.AwesomeCollect.service.junction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import webapp.AwesomeCollect.repository.junction.BaseActionTagJunctionRepository;

/**
 * アクションとタグの関係性のサービスクラスの基底クラス。
 *
 * @param <T> ジェネリクス
 */
public abstract class BaseActionTagJunctionService<T> {

  protected final BaseActionTagJunctionRepository<T> repository;

  public BaseActionTagJunctionService(BaseActionTagJunctionRepository<T> repository) {
    this.repository = repository;
  }

  /**
   * アクションIDを基にDBからタグIDリストを取得する。
   *
   * @param actionId アクションID
   * @return タグIDリスト
   */
  public List<Integer> prepareTagIdListByActionId(int actionId) {
    return repository.searchTagIdsByActionId(actionId);
  }

  /**
   * タグIDリストがnullまたは空の場合は何もしない。<br>
   * そうでない場合は、アクションIDとタグIDを組み合わせてエンティティを生成し、DBに登録する。
   *
   * @param actionId        アクションID
   * @param relationFactory エンティティを生成するインターフェース
   * @param tagIdList       タグIDリスト
   */
  public void registerNewRelations(
      int actionId, BiFunction<Integer, Integer, T> relationFactory,
      List<Integer> tagIdList) {

    if (tagIdList == null || tagIdList.isEmpty()) {
      return;
    }

    for (int tagId : tagIdList) {
      T relation = relationFactory.apply(actionId, tagId);
      registerRelation(relation);
    }
  }

  /**
   * タグIDリストがnullまたは空の場合は何もしない。<br>
   * そうでない場合は、アクションIDとタグIDを組み合わせてエンティティを生成し、
   * DBのレコードを更新（登録・削除）する。
   *
   * @param actionId        アクションID
   * @param relationFactory エンティティを生成するインターフェース
   * @param tagIdList       タグIDリスト
   */
  public void updateRelations(
      int actionId, BiFunction<Integer, Integer, T> relationFactory,
      List<Integer> tagIdList) {

    if (tagIdList == null || tagIdList.isEmpty()) {
      return;
    }

    List<Integer> currentTagIdList = prepareTagIdListByActionId(actionId);
    registerRelations(actionId, relationFactory, tagIdList, currentTagIdList);
    deleteRelations(actionId, relationFactory, tagIdList, currentTagIdList);
  }

  // 登録済みのタグ情報と入力されたタグ情報を比較し、差分をDBに登録する。
  private void registerRelations(
      int actionId, BiFunction<Integer, Integer, T> relationFactory,
      List<Integer> newTagIdList, List<Integer> currentTagIdList) {

    Set<Integer> toAddTagList = new HashSet<>(newTagIdList);
    currentTagIdList.forEach(toAddTagList :: remove);
    for (int tagId : toAddTagList) {
      T relation = relationFactory.apply(actionId, tagId);
      registerRelation(relation);
    }
  }

  // 登録済みのタグ情報と入力されたタグ情報を比較し、差分をDBから削除する。
  private void deleteRelations(
      int actionId, BiFunction<Integer, Integer, T> relationFactory,
      List<Integer> tagIdList, List<Integer> currentTagIdList) {

    Set<Integer> toRemoveList = new HashSet<>(currentTagIdList);
    tagIdList.forEach(toRemoveList :: remove);
    for (int tagId : toRemoveList) {
      T relation = relationFactory.apply(actionId, tagId);
      deleteRelationByRelatedId(relation);
    }
  }

  /**
   * アクションIDとタグIDの関係をDBに登録する。
   *
   * @param relation アクションIDとタグIDの関係
   */
  protected void registerRelation(T relation) {
    repository.registerRelation(relation);
  }

  /**
   * アクションIDを基に、アクションIDとタグIDの関係をDBから削除する。
   *
   * @param actionId アクションID
   */
  protected void deleteRelationByActionId(int actionId) {
    repository.deleteRelationByActionId(actionId);
  }

  /**
   * アクションIDとタグIDの関係を基に、DBのレコードを削除する。
   *
   * @param relation アクションIDとタグIDの関係
   */
  protected void deleteRelationByRelatedId(T relation) {
    repository.deleteRelationByRelatedId(relation);
  }
}
