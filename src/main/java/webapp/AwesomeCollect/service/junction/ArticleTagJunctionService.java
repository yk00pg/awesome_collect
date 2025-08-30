package webapp.AwesomeCollect.service.junction;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.junction.ArticleTagJunction;
import webapp.AwesomeCollect.mapper.junction.ArticleTagsJunctionMapper;

@Service
public class ArticleTagJunctionService extends BaseActionTagJunctionService<ArticleTagJunction> {

  public ArticleTagJunctionService(ArticleTagsJunctionMapper mapper) {
    super(mapper);
  }

  @Override
  public List<Integer> searchTagIdsByActionId(int articleId) {
    return super.searchTagIdsByActionId(articleId);
  }

  @Override
  @Transactional
  public void registerRelationIfNotExist(ArticleTagJunction relation) {
    super.registerRelationIfNotExist(relation);
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
