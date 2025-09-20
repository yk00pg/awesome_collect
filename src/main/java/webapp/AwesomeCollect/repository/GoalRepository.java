package webapp.AwesomeCollect.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.entity.action.Goal;
import webapp.AwesomeCollect.mapper.action.GoalMapper;

/**
 * 目標のリポジトリクラス。
 */
@Repository
public class GoalRepository {

  private final GoalMapper mapper;

  public GoalRepository(GoalMapper mapper){
    this.mapper = mapper;
  }

  public List<Goal> searchGoal(int userId){
    return mapper.selectGoal(userId);
  }

  public Goal findGoalByIds(int id, int userId){
    return mapper.selectGoalByIds(id, userId);
  }

  public void registerGoal(Goal goal){
    mapper.insertGoal(goal);
  }

  public void updateGoal(Goal goal){
    mapper.updateGoal(goal);
  }

  public void deleteGoal(int id){
    mapper.deleteGoal(id);
  }
}
