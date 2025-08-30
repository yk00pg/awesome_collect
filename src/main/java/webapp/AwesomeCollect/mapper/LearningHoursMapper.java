package webapp.AwesomeCollect.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import webapp.AwesomeCollect.dto.analysis.TagLearningHoursDto;
import webapp.AwesomeCollect.dto.analysis.TotalLearningHoursDto;

@Mapper
public interface LearningHoursMapper {

  @Select("""
      SELECT
        COALESCE(SUM(hours), 0) AS totalHours
      FROM
        daily_done
      WHERE
        user_id=#{userId}
      """)
  BigDecimal calculateTotalHours(int userId);

  @Select("""
      SELECT
        DAYOFWEEK(date) AS dayOfWeek,
        ROUND(AVG(hours) AS avgHours
      FROM
        daily_done
      WHERE
        user_id=#{userId}
      GROUP BY
        dayOfWeek
      ORDER BY
        daiOfWeek
      """)
  List<TotalLearningHoursDto> averageDayOfWeekHours(int userId);

  @Select("""
      SELECT
        date AS date,
        SUM(hours) AS totalHours
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
  List<TotalLearningHoursDto> calculateDailyTotalHours(
      int userId, LocalDate fromDate, LocalDate toDate);

  @Select("""
      SELECT
        DATE_SUB(date, INTERVAL DAY(date)-1 DAY) AS date,
        SUM(hours) AS totalHours
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
  List<TotalLearningHoursDto> calculateMonthlyTotalHours(
      int userId, LocalDate fromDate, LocalDate toDate);

  @Select("""
      SELECT
        COALESCE(t.id, 0) AS tagId,
        COALESCE(t.name, '(未設定)') AS tagName,
        SUM(dd.hours) AS totalHours
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
  List<TagLearningHoursDto> calculateTotalHoursByTag(int userId);
}
