package webapp.AwesomeCollect.service.junction;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.junction.DoneTagJunction;
import webapp.AwesomeCollect.repository.junction.DoneTagJunctionRepository;

/**
 * できたこととタグの関係性のサービスクラス。
 */
@Service
public class DoneTagJunctionService extends BaseActionTagJunctionService<DoneTagJunction> {

  private final DoneTagJunctionRepository doneTagJunctionRepository;

  public DoneTagJunctionService(DoneTagJunctionRepository repository) {
    super(repository);
    this.doneTagJunctionRepository = repository;
  }

  @Override
  public List<Integer> prepareTagIdListByActionId(int doneId) {
    return super.prepareTagIdListByActionId(doneId);
  }

  @Override
  public void registerNewRelations(
      int doneId, BiFunction<Integer, Integer, DoneTagJunction> relationFactory,
      List<Integer> tagIdList) {

    super.registerNewRelations(doneId, relationFactory, tagIdList);
  }

  @Override
  @Transactional
  public void updateRelations(
      int doneId, BiFunction<Integer, Integer, DoneTagJunction> relationFactory,
      List<Integer> tagIdList) {

    super.updateRelations(doneId, relationFactory, tagIdList);
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

  /**
   * 日付を基にDBからできたことIDとタグIDの関係を削除する。
   *
   * @param userId ユーザーID
   * @param date   日付
   */
  public void deleteRelationByDate(int userId, LocalDate date) {
    doneTagJunctionRepository.deleteRelationByDate(userId, date);
  }
}
