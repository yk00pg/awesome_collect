package webapp.AwesomeCollect.service.junction;

import java.util.List;
import webapp.AwesomeCollect.repository.junction.BaseActionTagJunctionRepository;

/**
 * 中間テーブルのサービスの基底クラス。
 *
 * @param <T> ジェネリクス
 */
public abstract class BaseActionTagJunctionService <T> {

  protected final BaseActionTagJunctionRepository<T> repository;

  public BaseActionTagJunctionService(BaseActionTagJunctionRepository<T> repository){
    this.repository = repository;
  }

  public List<Integer> prepareTagIdLitByActionId(int actionId){
    return repository.searchTagIdsByActionId(actionId);
  }

  public void registerRelationIfNotExist(T relation){
    if(!repository.isRegisteredRelation(relation)){
      repository.registerRelation(relation);
    }
  }

  public void deleteRelationByActionId(int actionId){
    repository.deleteRelationByActionId(actionId);
  }

  public void deleteRelationByRelatedId(T relation){
    repository.deleteRelationByRelatedId(relation);
  }
}
