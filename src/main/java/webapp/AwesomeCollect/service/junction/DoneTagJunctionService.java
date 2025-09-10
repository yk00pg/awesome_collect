package webapp.AwesomeCollect.service.junction;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.junction.DoneTagJunction;
import webapp.AwesomeCollect.repository.junction.DoneTagJunctionRepository;

/**
 * できたことタグのサービスクラス。
 */
@Service
public class DoneTagJunctionService extends BaseActionTagJunctionService<DoneTagJunction>{

  private final DoneTagJunctionRepository doneTagJunctionRepository;

  public DoneTagJunctionService(DoneTagJunctionRepository repository){
    super(repository);
    this.doneTagJunctionRepository = repository;
  }

  @Override
  public List<Integer> prepareTagIdLitByActionId(int doneId) {
    return super.prepareTagIdLitByActionId(doneId);
  }

  @Override
  @Transactional
  public void registerRelationIfNotExist(DoneTagJunction relation) {
    super.registerRelationIfNotExist(relation);
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
    doneTagJunctionRepository.deleteRelationByDate(userId, date);
  }
}
