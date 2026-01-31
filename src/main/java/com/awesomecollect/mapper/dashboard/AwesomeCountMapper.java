package com.awesomecollect.mapper.dashboard;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AwesomeCountMapper {

  @Select("""
      SELECT COUNT(*) FROM daily_todo
      WHERE user_id=#{userId}
      """)
  int countDailyTodo(int userId);

  @Select("""
      SELECT COUNT(*) FROM daily_done
      WHERE user_id=#{userId}
      """)
  int countDailyDone(int userId);

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

  @Select("""
      SELECT COUNT(*) FROM memo
      WHERE user_id=#{userId}
      """)
  int countMemo(int userId);

  @Select("""
      SELECT COUNT(*) FROM article_stock
      WHERE user_id=#{userId}
      """)
  int countArticleStock(int userId);

  @Select("""
      SELECT COUNT(finished) FROM article_stock
      WHERE user_id=#{userId} AND finished=1
      """)
  int countFinished(int userId);
}
