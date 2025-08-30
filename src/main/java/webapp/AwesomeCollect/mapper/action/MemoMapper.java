package webapp.AwesomeCollect.mapper.action;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import webapp.AwesomeCollect.entity.action.Memo;

@Mapper
public interface MemoMapper {

  @Select("""
      SELECT * FROM memo
      WHERE user_id=#{userId}
      ORDER BY id ASC
      """)
  List<Memo> selectMemo(int userId);

  @Select("""
      SELECT * FROM memo
      WHERE id=#{id} AND user_id=#{userId}
      """)
  Memo selectMemoByIds(int id, int userId);

  @Select("""
      SELECT COUNT(*) FROM memo
      WHERE user_id=#{userId}
      """)
  int countMemo(int userId);

  // @OptionsをつけてDBで自動採番されるIDを取得してエンティティに付与
  @Insert("""
      INSERT memo(user_id, title, content, registered_at)
      VALUES(#{userId}, #{title}, #{content}, #{registeredAt})
      """)
  @Options(useGeneratedKeys = true, keyProperty =  "id")
  int insertMemo(Memo memo);

  @Update("""
      UPDATE memo
      SET title=#{title}, content=#{content}, updated_at=#{updatedAt}
      WHERE id=#{id}
      """)
  int updateMemo(Memo memo);

  @Delete("""
      DELETE FROM memo
      WHERE id=#{id}
      """)
  int deleteMemo(int id);
}
