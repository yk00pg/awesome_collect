package webapp.AwesomeCollect.service.junction;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.junction.MemoTagJunction;
import webapp.AwesomeCollect.mapper.junction.BaseActionTagJunctionMapper;
import webapp.AwesomeCollect.mapper.junction.MemoTagJunctionMapper;

@Service
public class MemoTagJunctionService extends BaseActionTagJunctionService<MemoTagJunction> {

  public MemoTagJunctionService(MemoTagJunctionMapper mapper) {
    super(mapper);
  }

  @Override
  public List<Integer> searchTagIdsByActionId(int memoId) {
    return super.searchTagIdsByActionId(memoId);
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
