package webapp.AwesomeCollect.mapper;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import webapp.AwesomeCollect.entity.AvgLearningTime;
import webapp.AwesomeCollect.entity.TagLearningTime;
import webapp.AwesomeCollect.entity.TotalLearningTime;

@Mapper
public interface LearningTimeMapper {

  @Select("""
      SELECT
        COALESCE(SUM(minutes), 0) AS totalTime
      FROM
        daily_done
      WHERE
        user_id=#{userId}
      """)
  int calculateTotalTime(int userId);

  @Select("""
      SELECT
        DAYOFWEEK(date) AS dayOfWeek,
        ROUND(AVG(minutes)) AS avgTime
      FROM
        daily_done
      WHERE
        user_id=#{userId}
      GROUP BY
        dayOfWeek
      ORDER BY
        dayOfWeek
      """)
  List<AvgLearningTime> averageDayOfWeekTime(int userId);

  @Select("""
      SELECT
        date AS date,
        SUM(minutes) AS totalTime
      FROM
        daily_done
      WHERE
        user_id=#{userId}
        AND date BETWEEN #{fromDate} AND #{toDate}
      GROUP BY
        date
      ORDER BY
        date
      """)
  List<TotalLearningTime> calculateDailyTotalTime(
      int userId, LocalDate fromDate, LocalDate toDate);

  @Select("""
      SELECT
        DATE_SUB(date, INTERVAL DAY(date)-1 DAY) AS date,
        SUM(minutes) AS totalTime
      FROM
        daily_done
      WHERE
        user_id=#{userId}
        AND date BETWEEN #{fromDate} AND #{toDate}
      GROUP BY
        YEAR(date), MONTH(date), DATE_SUB(date, INTERVAL DAY(date)-1 DAY)
      ORDER BY
        DATE_SUB(date, INTERVAL DAY(date)-1 DAY)
      """)
  List<TotalLearningTime> calculateMonthlyTotalTime(
      int userId, LocalDate fromDate, LocalDate toDate);

  @Select("""
      SELECT
        COALESCE(t.id, 0) AS tagId,
        COALESCE(t.name, '(未設定)') AS tagName,
        SUM(dd.minutes) AS totalTime
      FROM
        daily_done dd
      LEFT JOIN
        done_tag dt ON dd.id = dt.done_id
      LEFT JOIN
        tag t ON dt.tag_id = t.id
      WHERE
        dd.user_id = #{userId}
      GROUP BY
        COALESCE(t.id, 0), COALESCE(t.name, '(未設定)')
      """)
  List<TagLearningTime> calculateTotalTimeByTag(int userId);
}
