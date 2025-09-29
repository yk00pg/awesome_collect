package webapp.AwesomeCollect.repository.action;

import java.util.List;
import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.entity.action.ArticleStock;
import webapp.AwesomeCollect.mapper.action.ArticleStockMapper;

/**
 * 記事ストックのリポジトリクラス。
 */
@Repository
public class ArticleStockRepository {

  private final ArticleStockMapper mapper;

  public ArticleStockRepository(ArticleStockMapper mapper) {
    this.mapper = mapper;
  }

  public List<ArticleStock> searchArticleStock(int userId) {
    return mapper.selectArticleStock(userId);
  }

  public ArticleStock findArticleStockByIds(int articleId, int userId) {
    return mapper.selectArticleStockByIds(articleId, userId);
  }

  public Integer findIdByUserIdAndTitle(int userId, String title) {
    return mapper.selectIdByUserIdAndTitle(userId, title);
  }

  public Integer findIdByUserIdAndUrl(int userId, String url) {
    return mapper.selectIdByUserIdAndUrl(userId, url);
  }

  public void registerArticleStock(ArticleStock articleStock) {
    mapper.insertArticleStock(articleStock);
  }

  public void updateArticleStock(ArticleStock articleStock) {
    mapper.updateArticleStock(articleStock);
  }

  public void deleteArticleStock(int articleId) {
    mapper.deleteArticleStock(articleId);
  }

}
