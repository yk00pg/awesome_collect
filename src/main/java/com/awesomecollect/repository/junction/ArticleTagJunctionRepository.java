package com.awesomecollect.repository.junction;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.awesomecollect.entity.junction.ArticleTagJunction;
import com.awesomecollect.mapper.junction.ArticleTagJunctionMapper;
import com.awesomecollect.provider.param.JunctionDeleteParams;

/**
 * 記事ストックとタグの関係性のリポジトリクラス。
 */
@Repository
public class ArticleTagJunctionRepository extends BaseActionTagJunctionRepository<ArticleTagJunction> {

  public ArticleTagJunctionRepository(ArticleTagJunctionMapper mapper) {
    super(mapper);
  }

  @Override
  public List<Integer> searchTagIdsByActionId(int articleId) {
    return super.searchTagIdsByActionId(articleId);
  }

  @Override
  public boolean isRegisteredRelation(ArticleTagJunction relation) {
    return super.isRegisteredRelation(relation);
  }

  @Override
  public void registerRelation(ArticleTagJunction relation) {
    super.registerRelation(relation);
  }

  @Override
  public void deleteRelationByActionId(int articleId) {
    super.deleteRelationByActionId(articleId);
  }

  @Override
  public void deleteRelationByRelatedId(ArticleTagJunction relation) {
    super.deleteRelationByRelatedId(relation);
  }

  @Override
  public void deleteAllRelationsByActionIdList(JunctionDeleteParams params) {
    super.deleteAllRelationsByActionIdList(params);
  }
}
