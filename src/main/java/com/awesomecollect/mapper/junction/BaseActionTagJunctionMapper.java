package com.awesomecollect.mapper.junction;

import java.util.List;
import com.awesomecollect.provider.param.JunctionDeleteParams;

public interface BaseActionTagJunctionMapper<T> {

  List<Integer> selectTagIds(int actionId);
  boolean isRegisteredRelation(T relation);
  void insertRelation(T relation);
  void deleteRelationByActionId(int actionId);
  void deleteRelationByRelatedId(T relation);
  void deleteAllRelationsByActionIdList(JunctionDeleteParams params);
}