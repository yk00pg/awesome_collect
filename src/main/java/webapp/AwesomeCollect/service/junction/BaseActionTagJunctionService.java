package webapp.AwesomeCollect.service.junction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import webapp.AwesomeCollect.repository.junction.BaseActionTagJunctionRepository;

/**
 * 中間テーブルのサービスクラスの基底クラス。
 *
 * @param <T> ジェネリクス
 */
public abstract class BaseActionTagJunctionService<T> {

  protected final BaseActionTagJunctionRepository<T> repository;

  public BaseActionTagJunctionService(BaseActionTagJunctionRepository<T> repository){
    this.repository = repository;
  }

  /**
   * アクションIDを基に
   * @param actionId
   * @return
   */
  public List<Integer> prepareTagIdListByActionId(int actionId){
    return repository.searchTagIdsByActionId(actionId);
  }

  public void registerNewRelations(
      int actionId, BiFunction<Integer, Integer, T> relationFactory,
      List<Integer> tagIdList){

    if(tagIdList == null || tagIdList.isEmpty()){
      return;
    }

    for(int tagId : tagIdList){
      T relation = relationFactory.apply(actionId, tagId);
      registerRelation(relation);
    }
  }

  public void updateRelations(
      int actionId, BiFunction<Integer, Integer, T> relationFactory,
      List<Integer> tagIdList){

    if(tagIdList == null || tagIdList.isEmpty()){
      return;
    }

    List<Integer> currentTagIdList = prepareTagIdListByActionId(actionId);

    registerRelations(actionId, relationFactory, tagIdList, currentTagIdList);
    deleteRelations(actionId, relationFactory, tagIdList, currentTagIdList);
  }

  private void deleteRelations(
      int actionId, BiFunction<Integer, Integer, T> relationFactory,
      List<Integer> tagIdList, List<Integer> currentTagIdList) {

    Set<Integer> toRemoveList = new HashSet<>(currentTagIdList);
    tagIdList.forEach(toRemoveList::remove);

    for(int tagId : toRemoveList){
      T relation = relationFactory.apply(actionId, tagId);
      deleteRelationByRelatedId(relation);
    }
  }

  private void registerRelations(
      int actionId, BiFunction<Integer, Integer, T> relationFactory,
      List<Integer> newTagIdList, List<Integer> currentTagIdList) {

    Set<Integer> toAddTagList = new HashSet<>(newTagIdList);
    currentTagIdList.forEach(toAddTagList::remove);

    for(int tagId : toAddTagList){
      T relation = relationFactory.apply(actionId, tagId);
      registerRelation(relation);
    }
  }

  protected void registerRelation(T relation){
    repository.registerRelation(relation);
  }

  public void deleteRelationByActionId(int actionId){
    repository.deleteRelationByActionId(actionId);
  }

  public void deleteRelationByRelatedId(T relation){
    repository.deleteRelationByRelatedId(relation);
  }
}
