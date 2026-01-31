package com.awesomecollect.mapper.action;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.awesomecollect.entity.action.DailyDone;

@Mapper
public interface DailyDoneMapper {

  @Select("""
      SELECT id FROM daily_done
      WHERE user_id=#{userId}
      """)
  List<Integer> selectIdByUserId(int userId);

  @Select("""
      SELECT * FROM daily_done
      WHERE user_id=#{userId} AND date=#{date}
      ORDER BY id ASC
      """)
  List<DailyDone> selectDailyDone(int userId, LocalDate date);

  // @OptionsをつけてDBで自動採番されるIDを取得してエンティティに付与
  @Insert("""
      INSERT daily_done(user_id, date, content, minutes, memo, registered_at)
      VALUES(#{userId}, #{date}, #{content}, #{minutes}, #{memo}, #{registeredAt})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertDone(DailyDone done);

  @Update("""
      UPDATE daily_done
      SET date=#{date}, content=#{content}, minutes=#{minutes}, memo=#{memo}, updated_at=#{updatedAt}
      WHERE id=#{id}
      """)
  void updateDone(DailyDone done);

  @Delete("""
      DELETE FROM daily_done
      WHERE id=#{id}
      """)
  void deleteDoneById(int id);

  @Delete("""
      DELETE FROM daily_done
      WHERE user_id=#{userId} AND date=#{date}
      """)
  void deleteDoneByDate(int userId, LocalDate date);

  @Delete("""
      DELETE FROM daily_done
      WHERE user_id=#{userId}
      """)
  void deleteAllDoneByUserId(int userId);
}
