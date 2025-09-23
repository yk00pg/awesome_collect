package webapp.AwesomeCollect.repository.junction;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.entity.junction.DoneTagJunction;
import webapp.AwesomeCollect.mapper.junction.DoneTagJunctionMapper;

/**
 * できたこと×タグのリポジトリクラス。
 */
@Repository
public class DoneTagJunctionRepository extends BaseActionTagJunctionRepository<DoneTagJunction>{

  private final DoneTagJunctionMapper doneTagJunctionMapper;

  public DoneTagJunctionRepository(DoneTagJunctionMapper mapper) {
    super(mapper);
    this.doneTagJunctionMapper = mapper;
  }

  @Override
  public List<Integer> searchTagIdsByActionId(int doneId){
    return super.searchTagIdsByActionId(doneId);
  }

  @Override
  public boolean isRegisteredRelation(DoneTagJunction relation) {
    return super.isRegisteredRelation(relation);
  }

  @Override
  public void registerRelation(DoneTagJunction relation) {
    super.registerRelation(relation);
  }

  @Override
  public void deleteRelationByActionId(int doneId) {
    super.deleteRelationByActionId(doneId);
  }

  @Override
  public void deleteRelationByRelatedId(DoneTagJunction relation) {
    super.deleteRelationByRelatedId(relation);
  }

  public void deleteRelationByDate(int userId, LocalDate date) {
    doneTagJunctionMapper.deleteRelationByDate(userId, date);
  }
}
