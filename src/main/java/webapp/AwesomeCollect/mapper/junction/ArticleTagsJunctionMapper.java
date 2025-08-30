package webapp.AwesomeCollect.mapper.junction;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import webapp.AwesomeCollect.entity.junction.ArticleTagJunction;

@Mapper
public interface ArticleTagsJunctionMapper extends BaseActionTagJunctionMapper<ArticleTagJunction> {

  @Select("""
      SELECT tag_id FROM article_tag
      WHERE article_id=#{articleId}
      """)
  List<Integer> selectTagIds(int articleId);

  @Select("""
      SELECT EXISTS(
        SELECT 1 FROM article_tag
        WHERE article_id=#{articleId} AND tag_id=#{tagId}
        )
      """)
  boolean isRegisteredRelation(ArticleTagJunction junction);

  @Insert("""
      INSERT article_tag(article_id, tag_id)
      VALUES(#{articleId}, #{tagId})
      """)
  int insertRelation(ArticleTagJunction junction);

  @Delete("""
      DELETE FROM article_tag
      WHERE article_id=#{articleId}
      """)
  int deleteRelationByActionId(int articleId);

  @Delete("""
      DELETE FROM article_tag
      WHERE article_tag=#{articleId} AND tag_id=#{tagId}
      """)
  int deleteRelationByRelatedId(ArticleTagJunction junction);
}
