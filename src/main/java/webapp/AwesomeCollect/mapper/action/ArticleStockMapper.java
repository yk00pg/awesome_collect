package webapp.AwesomeCollect.mapper.action;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import webapp.AwesomeCollect.entity.action.ArticleStock;

@Mapper
public interface ArticleStockMapper {

  @Select("""
      SELECT * FROM article_stock
      WHERE user_id=#{userId}
      """)
  List<ArticleStock> selectArticleStock(int userId);

  @Select("""
      SELECT * FROM article_stock
      WHERE id=#{id} AND user_id=#{userId}
      """)
  ArticleStock selectArticleStockByIds(int id, int userId);

  @Select("""
      SELECT COUNT(*) FROM article_stock
      WHERE user_id=#{userId}
      """)
  int countArticleStock(int userId);

  @Select("""
      SELECT COUNT(*) FROM article_stock
      WHERE user_id=#{userId} AND finished=1
      """)
  int countFinished(int userId);

  // @OptionsをつけてDBで自動採番されるIDを取得してエンティティに付与
  @Insert("""
      INSERT article_stock(user_id, title, url, finished, registered_at)
      VALUES(#{userId}, #{title}, #{url}, #{finished}, #{registeredAt})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insertArticleStock(ArticleStock articleStock);

  @Update("""
      UPDATE article_stock
      SET title=#{title}, url=#{url}, finished=#{finished}, updated_at=#{updatedAt}
      WHERE id=#{id}
      """)
  int updateArticleStock(ArticleStock articleStock);

  @Delete("""
      DELETE FROM article_stock
      WHERE id=#{id}
      """)
  int deleteArticleStock(int id);
}
