package webapp.AwesomeCollect.mapper.action;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import webapp.AwesomeCollect.entity.action.DailyDone;

@Mapper
public interface DailyDoneMapper {

  @Select("""
      SELECT * FROM daily_done
      WHERE user_id=#{userId} AND date=#{date}
      ORDER BY id ASC
      """)
  List<DailyDone> selectDailyDone(int userId, LocalDate date);

  @Select("""
      SELECT COUNT(*) FROM daily_done
      WHERE user_id=#{userId}
      """)
  int countDailyDone(int userId);

  // @OptionsをつけてDBで自動採番されるIDを取得してエンティティに付与
  @Insert("""
      INSERT daily_done(user_id, date, content, minutes, memo, registered_at)
      VALUES(#{userId}, #{date}, #{content}, #{minutes}, #{memo}, #{registeredAt})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insertDone(DailyDone done);

  @Update("""
      UPDATE daily_done
      SET date=#{date}, content=#{content}, minutes=#{minutes}, memo=#{memo}, updated_at=#{updatedAt}
      WHERE id=#{id}
      """)
  int updateDone(DailyDone done);

  @Delete("""
      DELETE FROM daily_done
      WHERE id=#{id}
      """)
  int deleteDoneById(int id);

  @Delete("""
      DELETE FROM daily_done
      WHERE user_id=#{userId} AND date=#{date}
      """)
  int deleteDoneByDate(int userId, LocalDate date);
}
