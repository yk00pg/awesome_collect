package webapp.AwesomeCollect.service.junction;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.junction.GoalTagJunction;
import webapp.AwesomeCollect.mapper.junction.BaseActionTagJunctionMapper;
import webapp.AwesomeCollect.mapper.junction.GoalTagJunctionMapper;

@Service
public class GoalTagJunctionService extends BaseActionTagJunctionService<GoalTagJunction> {

  public GoalTagJunctionService(GoalTagJunctionMapper mapper) {
    super(mapper);
  }

  @Override
  public List<Integer> searchTagIdsByActionId(int goalId) {
    return super.searchTagIdsByActionId(goalId);
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
