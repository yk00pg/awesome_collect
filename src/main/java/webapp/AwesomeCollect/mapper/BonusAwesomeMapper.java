package webapp.AwesomeCollect.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import webapp.AwesomeCollect.entity.BonusAwesome;

@Mapper
public interface BonusAwesomeMapper {

  @Select("""
      Select * FROM bonus_awesome
      WHERE user_id=#{userId}
      ORDER BY id ASC
      """)
  List<BonusAwesome> selectBonusAwesomeList(int userId);

  @Insert("""
      INSERT bonus_awesome(user_id, awesome_point, reason, collected_date)
      VALUES(#{userId}, #{awesomePoint}, #{reason}, #{collectedDate})
      """)
  int insertBonusAwesome(BonusAwesome bonusAwesome);
}
