package webapp.AwesomeCollect.mapper.junction;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import webapp.AwesomeCollect.entity.junction.DoneTagJunction;

@Mapper
public interface DoneTagsJunctionMapper extends BaseActionTagJunctionMapper<DoneTagJunction> {

  @Select("""
      SELECT tag_id FROM done_tag
      WHERE done_id=#{doneId}
      """)
  List<Integer> selectTagIds(int doneId);

  @Select("""
      SELECT EXISTS(
        SELECT 1 FROM done_tag
        WHERE done_id=#{doneId} AND tag_id=#{tagId}
        )
      """)
  boolean isRegisteredRelation(DoneTagJunction junction);

  @Insert("""
      INSERT done_tag(done_id, tag_id)
      VALUES(#{doneId}, #{tagId})
      """)
  int insertRelation(DoneTagJunction junction);

  @Delete("""
      DELETE FROM done_tag
      WHERE done_id=#{doneId}
      """)
  int deleteRelationByActionId(int doneId);

  @Delete("""
      DELETE FROM done_tag
      WHERE done_id=#{doneId} AND tag_id=#{tagId}
      """)
  int deleteRelationByRelatedId(DoneTagJunction junction);

  @Delete("""
      DELETE dt
      FROM done_tag AS dt
      INNER JOIN daily_done AS dd
      ON dt.done_id=dd.id
      WHERE dd.user_id=#{userId} AND dd.date=#{date}
      """)
  int deleteRelationByDate(int userId, LocalDate date);
}
