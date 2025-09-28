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
      SELECT id FROM article_stock
      WHERE user_id=#{userId} AND title=#{title}
      """)
  Integer selectIdByUserIdAndTitle(int userId, String title);

  @Select("""
      SELECT id FROM article_stock
      WHERE user_id=#{userId} AND url=#{url}
      """)
  Integer selectIdByUserIdAndUrl(int userId, String url);

  // @OptionsをつけてDBで自動採番されるIDを取得してエンティティに付与
  @Insert("""
      INSERT article_stock(user_id, title, url, memo, finished, registered_at)
      VALUES(#{userId}, #{title}, #{url}, #{memo}, #{finished}, #{registeredAt})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertArticleStock(ArticleStock articleStock);

  @Update("""
      UPDATE article_stock
      SET
        title=#{title}, url=#{url}, memo=#{memo}, finished=#{finished},
        updated_at=#{updatedAt}, status_updated_at=#{statusUpdatedAt}
      WHERE id=#{id}
      """)
  void updateArticleStock(ArticleStock articleStock);

  @Delete("""
      DELETE FROM article_stock
      WHERE id=#{id}
      """)
  void deleteArticleStock(int id);
}
