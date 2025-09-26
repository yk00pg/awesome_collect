package webapp.AwesomeCollect.service.junction;

import java.util.List;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.junction.GoalTagJunction;
import webapp.AwesomeCollect.repository.junction.GoalTagJunctionRepository;

/**
 * 目標とタグの関係性のサービスクラス。
 */
@Service
public class GoalTagJunctionService extends BaseActionTagJunctionService<GoalTagJunction> {

  public GoalTagJunctionService(GoalTagJunctionRepository repository) {
    super(repository);
  }

  @Override
  public List<Integer> prepareTagIdListByActionId(int goalId) {
    return super.prepareTagIdListByActionId(goalId);
  }

  @Override
  public void registerNewRelations(
      int actionId, BiFunction<Integer, Integer, GoalTagJunction> relationFactory,
      List<Integer> tagIdList) {

    super.registerNewRelations(actionId, relationFactory, tagIdList);
  }

  @Override
  @Transactional
  public void updateRelations(
      int actionId, BiFunction<Integer, Integer, GoalTagJunction> relationFactory,
      List<Integer> tagIdList) {

    super.updateRelations(actionId, relationFactory, tagIdList);
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
