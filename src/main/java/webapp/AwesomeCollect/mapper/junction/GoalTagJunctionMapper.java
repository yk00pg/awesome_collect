package webapp.AwesomeCollect.mapper.junction;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import webapp.AwesomeCollect.entity.junction.GoalTagJunction;

@Mapper
public interface GoalTagJunctionMapper extends BaseActionTagJunctionMapper<GoalTagJunction> {

  @Select("""
      SELECT tag_id FROM goal_tag
      WHERE goal_id=#{goalId}
      """)
  List<Integer> selectTagIds(int goalId);

  @Select("""
      SELECT EXISTS(
        SELECT 1 FROM goal_tag
        WHERE goal_id=#{goalId} AND tag_id=#{tagId}
        )
      """)
  boolean isRegisteredRelation(GoalTagJunction junction);

  @Insert("""
      INSERT goal_tag(goal_id, tag_id)
      VALUES(#{goalId}, #{tagId})
      """)
  int insertRelation(GoalTagJunction junction);

  @Delete("""
      DELETE FROM goal_tag
      WHERE goal_id=#{goalId}
      """)
  int deleteRelationByActionId(int goalId);

  @Delete("""
      DELETE FROM goal_tag
      WHERE goal_id=#{goalId} AND tag_id=#{tagId}
      """)
  int deleteRelationByRelatedId(GoalTagJunction junction);
}