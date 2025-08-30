package webapp.AwesomeCollect.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import webapp.AwesomeCollect.entity.UserProgress;

@Mapper
public interface UserProgressMapper {

  @Select("""
      SELECT * FROM user_progress
      WHERE user_id=#{userId}
      """)
  UserProgress selectUserProgress(int userId);

  @Insert("""
      INSERT user_progress(user_id, registered_date)
      VALUES(#{userId}, #{registeredDate})
      """)
  int insertUserProgress(UserProgress userProgress);

  @Update("""
      UPDATE user_progress
      SET total_action_days=#{totalActionDays}, last_action_date=#{lastActionDate},
      current_streak=#{currentStreak}, longest_streak=#{longestStreak}, streak_bonus_count=#{streakBonusCount}
      WHERE user_id=#{userId}
      """)
  int updateUserProgress(UserProgress userProgress);
}
