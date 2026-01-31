package com.awesomecollect.repository.junction;

import java.util.List;
import com.awesomecollect.mapper.junction.BaseActionTagJunctionMapper;
import com.awesomecollect.provider.param.JunctionDeleteParams;

/**
 * アクションとタグの関係性のリポジトリの基底クラス。
 *
 * @param <T>
 */
public abstract class BaseActionTagJunctionRepository<T> {

  protected final BaseActionTagJunctionMapper<T> mapper;

  public BaseActionTagJunctionRepository(BaseActionTagJunctionMapper<T> mapper) {
    this.mapper = mapper;
  }

  public List<Integer> searchTagIdsByActionId(int actionId) {
    return mapper.selectTagIds(actionId);
  }

  public boolean isRegisteredRelation(T relation) {
    return mapper.isRegisteredRelation(relation);
  }

  public void registerRelation(T relation) {
    mapper.insertRelation(relation);
  }

  public void deleteRelationByActionId(int actionId) {
    mapper.deleteRelationByActionId(actionId);
  }

  public void deleteRelationByRelatedId(T relation) {
    mapper.deleteRelationByRelatedId(relation);
  }

  public void deleteAllRelationsByActionIdList(JunctionDeleteParams params){
    mapper.deleteAllRelationsByActionIdList(params);
  }
}
