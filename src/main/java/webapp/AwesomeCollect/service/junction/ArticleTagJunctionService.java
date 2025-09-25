package webapp.AwesomeCollect.service.junction;

import java.util.List;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.junction.ArticleTagJunction;
import webapp.AwesomeCollect.repository.junction.ArticleTagJunctionRepository;

/**
 * 記事ストック×タグのサービスクラス。
 */
@Service
public class ArticleTagJunctionService extends BaseActionTagJunctionService<ArticleTagJunction> {

  public ArticleTagJunctionService(ArticleTagJunctionRepository repository) {
    super(repository);
  }

  @Override
  public List<Integer> prepareTagIdListByActionId(int articleId) {
    return super.prepareTagIdListByActionId(articleId);
  }

  @Override
  public void registerNewRelations(
      int actionId, BiFunction<Integer, Integer, ArticleTagJunction> relationFactory,
      List<Integer> tagIdList) {

    super.registerNewRelations(actionId, relationFactory, tagIdList);
  }

  @Override
  @Transactional
  public void updateRelations(
      int actionId, BiFunction<Integer, Integer, ArticleTagJunction> relationFactory,
      List<Integer> tagIdList) {

    super.updateRelations(actionId, relationFactory, tagIdList);
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
}
