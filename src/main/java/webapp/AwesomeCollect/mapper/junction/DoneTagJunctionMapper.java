package webapp.AwesomeCollect.mapper.junction;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import webapp.AwesomeCollect.entity.junction.DoneTagJunction;
import webapp.AwesomeCollect.provider.ActionTagJunctionProvider;
import webapp.AwesomeCollect.provider.param.JunctionDeleteParams;

@Mapper
public interface DoneTagJunctionMapper extends BaseActionTagJunctionMapper<DoneTagJunction> {

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
  boolean isRegisteredRelation(DoneTagJunction relation);

  @Insert("""
      INSERT done_tag(done_id, tag_id)
      VALUES(#{doneId}, #{tagId})
      """)
  void insertRelation(DoneTagJunction relation);

  @Delete("""
      DELETE FROM done_tag
      WHERE done_id=#{doneId}
      """)
  void deleteRelationByActionId(int doneId);

  @Delete("""
      DELETE FROM done_tag
      WHERE done_id=#{doneId} AND tag_id=#{tagId}
      """)
  void deleteRelationByRelatedId(DoneTagJunction relation);

  @Delete("""
      DELETE dt
      FROM done_tag AS dt
      INNER JOIN daily_done AS dd
      ON dt.done_id=dd.id
      WHERE dd.user_id=#{userId} AND dd.date=#{date}
      """)
  void deleteRelationByDate(int userId, LocalDate date);

  @DeleteProvider(type = ActionTagJunctionProvider.class, method = "deleteRelationsByActionIdList")
  void deleteAllRelationsByActionIdList(JunctionDeleteParams params);
}
