package com.awesomecollect.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import com.awesomecollect.entity.Tag;
import com.awesomecollect.provider.TagProvider;

@Mapper
public interface TagMapper {

  @Select("""
      SELECT name FROM tag
      WHERE user_Id=#{userId}
      """)
  List<String> selectTagNameListByUserId(int userId);

  // WHERE IN句に変数を用いるためにプロバイダを通す
  @SelectProvider(type = TagProvider.class, method = "selectIdsByTagIdList")
  List<String> selectTagNameListByTagIdList(@Param("tagIdList") List<Integer> tagIdList);

  @Select("""
      SELECT id FROM tag
      WHERE user_id=#{userId} AND name=#{name}
      """)
  Integer findTagIdByUserIdAndTagName(int userId, String name);

  // @OptionsをつけてDBで自動採番されるIDを取得してエンティティに付与
  @Insert("""
      INSERT tag(user_id, name)
      VALUES(#{userId}, #{name})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertTag(Tag tag);

  @Delete("""
      DELETE FROM tag
      WHERE user_id=#{userId}
      """)
  void deleteAllTagByUserId(int userId);
}
