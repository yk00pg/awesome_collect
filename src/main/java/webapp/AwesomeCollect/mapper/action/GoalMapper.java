package webapp.AwesomeCollect.mapper.action;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import webapp.AwesomeCollect.entity.action.Goal;

@Mapper
public interface GoalMapper {

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
      SELECT COUNT(*) FROM goal
      WHERE user_id=#{userId}
      """)
  int countGoal(int userId);

  @Select("""
      SELECT COUNT(achieved) FROM goal
      WHERE user_id=#{userId} AND achieved=1
      """)
  int countAchieved(int userId);

  // @OptionsをつけてDBで自動採番されるIDを取得してエンティティに付与
  @Insert("""
      INSERT goal(user_id, title, content, achieved, registered_at)
      VALUES(#{userId}, #{title}, #{content}, #{achieved}, #{registeredAt})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insertGoal(Goal goal);

  @Update("""
      UPDATE goal
      SET title=#{title}, content=#{content}, achieved=#{achieved}, updated_at=#{updatedAt}
      WHERE id=#{id}
      """)
  int updateGoal(Goal goal);

  @Delete("""
      DELETE FROM goal
      WHERE id=#{id}
      """)
  int deleteGoal(int id);
}
