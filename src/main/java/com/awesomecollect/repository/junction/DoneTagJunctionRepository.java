package com.awesomecollect.repository.junction;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.awesomecollect.entity.junction.DoneTagJunction;
import com.awesomecollect.mapper.junction.DoneTagJunctionMapper;
import com.awesomecollect.provider.param.JunctionDeleteParams;

/**
 * できたこととタグの関係性のリポジトリクラス。
 */
@Repository
public class DoneTagJunctionRepository extends BaseActionTagJunctionRepository<DoneTagJunction> {

  private final DoneTagJunctionMapper doneTagJunctionMapper;

  public DoneTagJunctionRepository(DoneTagJunctionMapper mapper) {
    super(mapper);
    this.doneTagJunctionMapper = mapper;
  }

  @Override
  public List<Integer> searchTagIdsByActionId(int doneId) {
    return super.searchTagIdsByActionId(doneId);
  }

  @Override
  public boolean isRegisteredRelation(DoneTagJunction relation) {
    return super.isRegisteredRelation(relation);
  }

  @Override
  public void registerRelation(DoneTagJunction relation) {
    super.registerRelation(relation);
  }

  @Override
  public void deleteRelationByActionId(int doneId) {
    super.deleteRelationByActionId(doneId);
  }

  @Override
  public void deleteRelationByRelatedId(DoneTagJunction relation) {
    super.deleteRelationByRelatedId(relation);
  }

  @Override
  public void deleteAllRelationsByActionIdList(JunctionDeleteParams params) {
    super.deleteAllRelationsByActionIdList(params);
  }

  public void deleteRelationByDate(int userId, LocalDate date) {
    doneTagJunctionMapper.deleteRelationByDate(userId, date);
  }
}
