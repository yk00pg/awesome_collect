package webapp.AwesomeCollect.service.action;

import java.util.List;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.mapper.action.ArticleStockMapper;
import webapp.AwesomeCollect.entity.action.ArticleStock;

@Service
public class ArticleStockService {

  private final ArticleStockMapper mapper;

  public ArticleStockService(ArticleStockMapper mapper){
    this.mapper = mapper;
  }

  public List<ArticleStock> searchArticleStock(int userId){
    return mapper.selectArticleStock(userId);
  }

  public ArticleStock findArticleStockByIds(int id, int userId){
    return mapper.selectArticleStockByIds(id, userId);
  }

  public int countArticleStock(int userId){
    return mapper.countArticleStock(userId);
  }

  public int countFinished(int userId){
    return mapper.countFinished(userId);
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
