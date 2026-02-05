package com.awesomecollect.repository.action;

import com.awesomecollect.entity.action.Goal;
import com.awesomecollect.mapper.action.GoalMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * 目標のリポジトリクラス。
 */
@Repository
public class GoalRepository {

  private final GoalMapper mapper;

  public GoalRepository(GoalMapper mapper) {
    this.mapper = mapper;
  }

  public List<Integer> searchIdByUserId(int userId){
    return mapper.selectIdByUserId(userId);
  }

  public List<Goal> searchGoal(int userId) {
    return mapper.selectGoal(userId);
  }

  public Optional<Goal> findGoalByIds(int goalId, int userId) {
    return Optional.ofNullable(mapper.selectGoalByIds(goalId, userId));
  }

  public Integer findIdByUserIdAndTitle(int userId, String title) {
    return mapper.selectIdByUserIdAndTitle(userId, title);
  }

  public void registerGoal(Goal goal) {
    mapper.insertGoal(goal);
  }

  public void updateGoal(Goal goal) {
    mapper.updateGoal(goal);
  }

  public void deleteGoal(int goalId) {
    mapper.deleteGoal(goalId);
  }

  public void deleteAllGoalByUserId(int userId){
    mapper.deleteAllGoalByUserId(userId);
  }
}
