package webapp.AwesomeCollect.service.junction;

import java.util.List;
import webapp.AwesomeCollect.mapper.junction.BaseActionTagJunctionMapper;

public abstract class BaseActionTagJunctionService <T> {

  protected final BaseActionTagJunctionMapper<T> mapper;

  public BaseActionTagJunctionService(BaseActionTagJunctionMapper<T> mapper){
    this.mapper = mapper;
  }

  public List<Integer> searchTagIdsByActionId(int actionId){
    return mapper.selectTagIds(actionId);
  }

  public void registerRelationIfNotExist(T relation){
    if(!mapper.isRegisteredRelation(relation)){
      mapper.insertRelation(relation);
    }
  }

  public void deleteRelationByActionId(int actionId){
    mapper.deleteRelationByActionId(actionId);
  }

  public void deleteRelationByRelatedId(T relation){
    mapper.deleteRelationByRelatedId(relation);
  }
}
