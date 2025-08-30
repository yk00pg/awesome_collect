package webapp.AwesomeCollect.mapper.junction;

import java.util.List;

public interface BaseActionTagJunctionMapper<T> {

  List<Integer> selectTagIds(int actionId);
  boolean isRegisteredRelation(T junction);
  int insertRelation(T junction);
  int deleteRelationByActionId(int actionId);
  int deleteRelationByRelatedId(T junction);

}
