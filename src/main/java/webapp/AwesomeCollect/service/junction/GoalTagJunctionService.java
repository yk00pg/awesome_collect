package webapp.AwesomeCollect.service.junction;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.junction.GoalTagJunction;
import webapp.AwesomeCollect.mapper.junction.GoalTagJunctionMapper;
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
  public void registerRelationIfNotExist(GoalTagJunction relation) {
    super.registerRelationIfNotExist(relation);
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
