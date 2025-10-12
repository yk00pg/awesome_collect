package webapp.AwesomeCollect.mapper.junction;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import webapp.AwesomeCollect.entity.junction.MemoTagJunction;
import webapp.AwesomeCollect.provider.ActionTagJunctionProvider;
import webapp.AwesomeCollect.provider.param.JunctionDeleteParams;

@Mapper
public interface MemoTagJunctionMapper extends BaseActionTagJunctionMapper<MemoTagJunction> {

  @Select("""
      SELECT tag_id FROM memo_tag
      WHERE memo_id=#{memoId}
      """)
  List<Integer> selectTagIds(int memoId);

  @Select("""
      SELECT EXISTS(
        SELECT 1 FROM memo_tag
        WHERE memo_id=#{memoId} AND tag_id=#{tagId}
        )
      """)
  boolean isRegisteredRelation(MemoTagJunction relation);

  @Insert("""
      INSERT memo_tag(memo_id, tag_id)
      VALUES(#{memoId}, #{tagId})
      """)
  void insertRelation(MemoTagJunction relation);

  @Delete("""
      DELETE FROM memo_tag
      WHERE memo_id=#{memoId}
      """)
  void deleteRelationByActionId(int memoId);

  @Delete("""
      DELETE FROM memo_tag
      WHERE memo_id=#{memoId} AND tag_id=#{tagId}
      """)
  void deleteRelationByRelatedId(MemoTagJunction relation);

  @DeleteProvider(type = ActionTagJunctionProvider.class, method = "deleteRelationsByActionIdList")
  void deleteAllRelationsByActionIdList(JunctionDeleteParams params);
}
