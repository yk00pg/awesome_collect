package webapp.AwesomeCollect.mapper.junction;

import java.util.List;

public interface BaseActionTagJunctionMapper<T> {

  List<Integer> selectTagIds(int actionId);
  boolean isRegisteredRelation(T relation);
  void insertRelation(T relation);
  void deleteRelationByActionId(int actionId);
  void deleteRelationByRelatedId(T relation);
}