package webapp.AwesomeCollect.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import webapp.AwesomeCollect.entity.Tag;

@Mapper
public interface TagMapper {

  @Select("""
      SELECT name FROM tag
      WHERE user_Id=#{userId}
      """)
  List<String> selectTagList(int userId);

  @Select("""
      SELECT name FROM tag
      WHERE id=#{id}
      """)
  String selectTagName(int id);

  @Select("""
      SELECT id FROM tag
      WHERE user_id=#{userId} AND name=#{name}
      """)
  Integer selectIdByUserIdAndTagName(int userId, String name);

  // @OptionsをつけてDBで自動採番されるIDを取得してエンティティに付与
  @Insert("""
      INSERT tag(user_id, name)
      VALUES(#{userId}, #{name})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insertTag(Tag tag);

  @Update("""
      UPDATE tag
      SET name=#{name}
      WHERE id=#{id}
      """)
  int updateTag(Tag tag);

  @Delete("""
      DELETE FROM tag
      WHERE id=#{id}
      """)
  int deleteTag(Tag tag);
}
