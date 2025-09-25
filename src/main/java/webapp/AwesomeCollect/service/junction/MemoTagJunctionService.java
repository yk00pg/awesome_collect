package webapp.AwesomeCollect.service.junction;

import java.util.List;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.junction.MemoTagJunction;
import webapp.AwesomeCollect.repository.junction.MemoTagJunctionRepository;

/**
 * メモ×タグのサービスクラス。
 */
@Service
public class MemoTagJunctionService extends BaseActionTagJunctionService<MemoTagJunction> {

  public MemoTagJunctionService(MemoTagJunctionRepository repository) {
    super(repository);
  }

  @Override
  public List<Integer> prepareTagIdListByActionId(int memoId) {
    return super.prepareTagIdListByActionId(memoId);
  }

  @Override
  public void registerNewRelations(
      int actionId, BiFunction<Integer, Integer, MemoTagJunction> relationFactory,
      List<Integer> tagIdList) {

    super.registerNewRelations(actionId, relationFactory, tagIdList);
  }

  @Override
  @Transactional
  public void updateRelations(
      int actionId, BiFunction<Integer, Integer, MemoTagJunction> relationFactory,
      List<Integer> tagIdList) {

    super.updateRelations(actionId, relationFactory, tagIdList);
  }

  @Override
  public void registerRelation(MemoTagJunction relation) {
    super.registerRelation(relation);
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
