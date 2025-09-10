package webapp.AwesomeCollect.service.junction;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.junction.MemoTagJunction;
import webapp.AwesomeCollect.mapper.junction.MemoTagJunctionMapper;
import webapp.AwesomeCollect.repository.junction.MemoTagJunctionRepository;

/**
 * メモタグのサービスクラス。
 */
@Service
public class MemoTagJunctionService extends BaseActionTagJunctionService<MemoTagJunction> {

  public MemoTagJunctionService(MemoTagJunctionRepository repository) {
    super(repository);
  }

  @Override
  public List<Integer> prepareTagIdLitByActionId(int memoId) {
    return super.prepareTagIdLitByActionId(memoId);
  }

  @Override
  @Transactional
  public void registerRelationIfNotExist(MemoTagJunction relation) {
    super.registerRelationIfNotExist(relation);
  }

  @Override
  public void deleteRelationByActionId(int memoId) {
    super.deleteRelationByActionId(memoId);
  }

  @Override
  public void deleteRelationByRelatedId(MemoTagJunction relation) {
    super.deleteRelationByRelatedId(relation);
  }
}
