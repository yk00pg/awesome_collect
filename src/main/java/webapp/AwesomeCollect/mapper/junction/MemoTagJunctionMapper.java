package webapp.AwesomeCollect.mapper.junction;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import webapp.AwesomeCollect.entity.junction.MemoTagJunction;

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
  boolean isRegisteredRelation(MemoTagJunction junction);

  @Insert("""
      INSERT memo_tag(memo_id, tag_id)
      VALUES(#{memoId}, #{tagId})
      """)
  int insertRelation(MemoTagJunction junction);

  @Delete("""
      DELETE FROM memo_tag
      WHERE memo_id=#{memoId}
      """)
  int deleteRelationByActionId(int memoId);

  @Delete("""
      DELETE FROM memo_tag
      WHERE memo_id=#{memoId} AND tag_id=#{tagId}
      """)
  int deleteRelationByRelatedId(MemoTagJunction junction);
}
