package webapp.AwesomeCollect.service.action;

import java.util.List;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.mapper.action.GoalMapper;
import webapp.AwesomeCollect.entity.action.Goal;

@Service
public class GoalService {

  private final GoalMapper mapper;

  public GoalService(GoalMapper mapper){
    this.mapper = mapper;
  }

  public List<Goal> searchGoal(int userId){
    return mapper.selectGoal(userId);
  }

  public Goal findGoalByIds(int id, int userId){
    return mapper.selectGoalByIds(id, userId);
  }

  public int countGoal(int userId){
    return mapper.countGoal(userId);
  }

  public int countAchieved(int userId){
    return mapper.countAchieved(userId);
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
