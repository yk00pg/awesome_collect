package webapp.AwesomeCollect.repository;

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

  public ArticleStockRepository(ArticleStockMapper mapper){
    this.mapper = mapper;
  }

  public List<ArticleStock> searchArticleStock(int userId){
    return mapper.selectArticleStock(userId);
  }

  public ArticleStock findArticleStockByIds(int id, int userId){
    return mapper.selectArticleStockByIds(id, userId);
  }

  public void registerArticleStock(ArticleStock articleStock){
    mapper.insertArticleStock(articleStock);
  }

  public void updateArticleStock(ArticleStock articleStock){
    mapper.updateArticleStock(articleStock);
  }

  public void deleteArticleStock(int id){
    mapper.deleteArticleStock(id);
  }

}
