package webapp.AwesomeCollect.mapper.action;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import webapp.AwesomeCollect.entity.action.DailyTodo;

@Mapper
public interface DailyTodoMapper {

  @Select("""
      SELECT * FROM daily_todo
      WHERE user_id=#{userId} AND date=#{date}
      """)
  List<DailyTodo> selectDailyTodo(int userId, LocalDate date);


  @Select("""
      SELECT COUNT(*) FROM daily_todo
      WHERE user_id=#{userId}
      """)
  int countDailyTodo(int userId);

  @Insert("""
      INSERT daily_todo(user_id, date, content, registered_at)
      VALUES(#{userId}, #{date}, #{content}, #{registeredAt})
      """)
  int insertTodo(DailyTodo todo);

  @Update("""
      UPDATE daily_todo
      SET date=#{date}, content=#{content}, updated_at=#{updatedAt}
      WHERE id=#{id}
      """)
  int updateTodo(DailyTodo todo);

  @Delete("""
      DELETE FROM daily_todo
      WHERE id=#{id}
      """)
  int deleteTodoById(int id);

  @Delete("""
      DELETE FROM daily_todo
      WHERE user_id=#{userId} AND date=#{date}
      """)
  int deleteTodoByDate(int userId, LocalDate date);
}
