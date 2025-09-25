package webapp.AwesomeCollect.mapper.dashboard;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LearningDaysMapper {

  @Select("""
      SELECT
        COUNT(DISTINCT date) AS totalDate
      FROM
        daily_done
      WHERE
        user_id=#{userId}
      """)
  int countTotalLearningDate(int userId);

  @Select("""
      SELECT
        DISTINCT date
      FROM
        daily_done
      WHERE
        user_id=#{userId}
      ORDER BY
        date
      """)
  List<LocalDate> selectDistinctLearningDate(int userId);
}
