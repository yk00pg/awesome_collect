package webapp.AwesomeCollect.mapper.user;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import webapp.AwesomeCollect.entity.user.UserProgress;
import webapp.AwesomeCollect.provider.UserProgressProvider;
import webapp.AwesomeCollect.provider.param.ExpiredGuestUserParams;

@Mapper
public interface UserProgressMapper {

  @SelectProvider(type = UserProgressProvider.class, method = "selectUserIdByUserIdList")
  List<Integer> selectExpiredUserIdByUserIdList(ExpiredGuestUserParams params);

  @Select("""
      SELECT * FROM user_progress
      WHERE user_id=#{userId}
      """)
  UserProgress selectUserProgress(int userId);

  @Insert("""
      INSERT user_progress(user_id, registered_date)
      VALUES(#{userId}, #{registeredDate})
      """)
  void insertUserProgress(UserProgress userProgress);

  @Update("""
      UPDATE user_progress
      SET
        total_action_days=#{totalActionDays}, last_action_date=#{lastActionDate},
        current_streak=#{currentStreak}, longest_streak=#{longestStreak},
        streak_bonus_count=#{streakBonusCount}
      WHERE user_id=#{userId}
      """)
  void updateUserProgress(UserProgress userProgress);

  @Delete("""
      DELETE FROM user_progress
      WHERE user_id=#{userId}
      """)
  void deleteUserProgressByUserId(int userId);
}
