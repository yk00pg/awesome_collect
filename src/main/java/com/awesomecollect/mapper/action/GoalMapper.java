package com.awesomecollect.mapper.action;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.awesomecollect.entity.action.Goal;

@Mapper
public interface GoalMapper {

  @Select("""
      SELECT id FROM goal
      WHERE user_id=#{userId}
      """)
  List<Integer> selectIdByUserId(int userId);

  @Select("""
      SELECT * FROM goal
      WHERE user_id=#{userId}
      ORDER BY id ASC
      """)
  List<Goal> selectGoal(int userId);

  @Select("""
      SELECT * FROM goal
      WHERE id=#{id} AND user_id=#{userId}
      """)
  Goal selectGoalByIds(int id, int userId);

  @Select("""
      SELECT id FROM goal
      WHERE user_id=#{userId} AND title=#{title}
      """)
  Integer selectIdByUserIdAndTitle(int userId, String title);

  // @OptionsをつけてDBで自動採番されるIDを取得してエンティティに付与
  @Insert("""
      INSERT goal(user_id, title, content, achieved, registered_at)
      VALUES(#{userId}, #{title}, #{content}, #{achieved}, #{registeredAt})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertGoal(Goal goal);

  @Update("""
      UPDATE goal
      SET
        title=#{title}, content=#{content}, achieved=#{achieved},
        updated_at=#{updatedAt}, status_updated_at=#{statusUpdatedAt}
      WHERE id=#{id}
      """)
  void updateGoal(Goal goal);

  @Delete("""
      DELETE FROM goal
      WHERE id=#{id}
      """)
  void deleteGoal(int id);

  @Delete("""
      DELETE FROM goal
      WHERE user_id=#{userId}
      """)
  void deleteAllGoalByUserId(int userId);
}
