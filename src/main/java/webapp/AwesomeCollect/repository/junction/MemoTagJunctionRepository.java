package webapp.AwesomeCollect.repository.junction;

import java.util.List;
import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.entity.junction.MemoTagJunction;
import webapp.AwesomeCollect.mapper.junction.MemoTagJunctionMapper;

/**
 * メモタグのリポジトリクラス。
 */
@Repository
public class MemoTagJunctionRepository extends BaseActionTagJunctionRepository<MemoTagJunction> {

  public MemoTagJunctionRepository(MemoTagJunctionMapper mapper) {
    super(mapper);
  }

  @Override
  public List<Integer> searchTagIdsByActionId(int memoId) {
    return super.searchTagIdsByActionId(memoId);
  }

  @Override
  public boolean isRegisteredRelation(MemoTagJunction relation) {
    return super.isRegisteredRelation(relation);
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
