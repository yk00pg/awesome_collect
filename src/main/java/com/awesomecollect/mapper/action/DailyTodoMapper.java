package com.awesomecollect.mapper.action;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.awesomecollect.entity.action.DailyTodo;

@Mapper
public interface DailyTodoMapper {

  @Select("""
      SELECT * FROM daily_todo
      WHERE user_id=#{userId} AND date=#{date}
      """)
  List<DailyTodo> selectDailyTodo(int userId, LocalDate date);

  @Insert("""
      INSERT daily_todo(user_id, date, content, registered_at)
      VALUES(#{userId}, #{date}, #{content}, #{registeredAt})
      """)
  void insertTodo(DailyTodo todo);

  @Update("""
      UPDATE daily_todo
      SET date=#{date}, content=#{content}, updated_at=#{updatedAt}
      WHERE id=#{id}
      """)
  void updateTodo(DailyTodo todo);

  @Delete("""
      DELETE FROM daily_todo
      WHERE id=#{id}
      """)
  void deleteTodoById(int id);

  @Delete("""
      DELETE FROM daily_todo
      WHERE user_id=#{userId} AND date=#{date}
      """)
  void deleteTodoByDate(int userId, LocalDate date);

  @Delete("""
      DELETE FROM daily_todo
      WHERE user_id=#{userId}
      """)
  void deleteAllTodoByUserId(int userId);
}
