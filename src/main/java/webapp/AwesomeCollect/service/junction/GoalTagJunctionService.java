package webapp.AwesomeCollect.service.junction;

import java.util.List;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.junction.GoalTagJunction;
import webapp.AwesomeCollect.repository.junction.GoalTagJunctionRepository;

/**
 * 目標タグのサービスクラス。
 */
@Service
public class GoalTagJunctionService extends BaseActionTagJunctionService<GoalTagJunction> {

  public GoalTagJunctionService(GoalTagJunctionRepository repository) {
    super(repository);
  }

  @Override
  public List<Integer> prepareTagIdLitByActionId(int goalId) {
    return super.prepareTagIdLitByActionId(goalId);
  }

  @Override
  @Transactional
  public void saveRelations(
      int actionId, BiFunction<Integer, Integer, GoalTagJunction> relationFactory,
      List<Integer> newTagIdList) {

    super.saveRelations(actionId, relationFactory, newTagIdList);
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
