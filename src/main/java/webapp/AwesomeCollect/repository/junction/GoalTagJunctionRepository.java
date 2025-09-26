package webapp.AwesomeCollect.repository.junction;

import java.util.List;
import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.entity.junction.GoalTagJunction;
import webapp.AwesomeCollect.mapper.junction.GoalTagJunctionMapper;

/**
 * 目標とタグの関係性のリポジトリクラス。
 */
@Repository
public class GoalTagJunctionRepository extends BaseActionTagJunctionRepository<GoalTagJunction> {

  public GoalTagJunctionRepository(GoalTagJunctionMapper mapper) {
    super(mapper);
  }

  @Override
  public List<Integer> searchTagIdsByActionId(int goalId) {
    return super.searchTagIdsByActionId(goalId);
  }

  @Override
  public boolean isRegisteredRelation(GoalTagJunction relation) {
    return super.isRegisteredRelation(relation);
  }

  @Override
  public void registerRelation(GoalTagJunction relation) {
    super.registerRelation(relation);
  }

  @Override
  public void deleteRelationByActionId(int goalId) {
    super.deleteRelationByActionId(goalId);
  }

  @Override
  public void deleteRelationByRelatedId(GoalTagJunction relation) {
    super.deleteRelationByRelatedId(relation);
  }
}
