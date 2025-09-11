package webapp.AwesomeCollect.service.junction;

import java.util.List;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.junction.ArticleTagJunction;
import webapp.AwesomeCollect.repository.junction.ArticleTagJunctionRepository;

/**
 * 記事ストックタグのサービスクラス。
 */
@Service
public class ArticleTagJunctionService extends BaseActionTagJunctionService<ArticleTagJunction> {

  public ArticleTagJunctionService(ArticleTagJunctionRepository repository) {
    super(repository);
  }

  @Override
  public List<Integer> prepareTagIdLitByActionId(int articleId) {
    return super.prepareTagIdLitByActionId(articleId);
  }

  @Override
  @Transactional
  public void saveRelations(
      int actionId, BiFunction<Integer, Integer, ArticleTagJunction> relationFactory,
      List<Integer> newTagIdList) {

    super.saveRelations(actionId, relationFactory, newTagIdList);
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
