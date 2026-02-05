package com.awesomecollect.repository.action;

import com.awesomecollect.entity.action.ArticleStock;
import com.awesomecollect.mapper.action.ArticleStockMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * 記事ストックのリポジトリクラス。
 */
@Repository
public class ArticleStockRepository {

  private final ArticleStockMapper mapper;

  public ArticleStockRepository(ArticleStockMapper mapper) {
    this.mapper = mapper;
  }

  public List<Integer> searchIdByUserId(int userId){
    return mapper.selectIdByUserId(userId);
  }

  public List<ArticleStock> searchArticleStock(int userId) {
    return mapper.selectArticleStock(userId);
  }

  public Optional<ArticleStock> findArticleStockByIds(int articleId, int userId) {
    return Optional.ofNullable(mapper.selectArticleStockByIds(articleId, userId));
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

  public void deleteAllArticleStockByUserId(int userId){
    mapper.deleteAllArticleStockByUserId(userId);
  }
}
