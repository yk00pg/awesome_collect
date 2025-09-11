package webapp.AwesomeCollect.service.junction;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.junction.DoneTagJunction;
import webapp.AwesomeCollect.mapper.junction.DoneTagsJunctionMapper;

@Service
public class DoneTagJunctionService extends BaseActionTagJunctionService<DoneTagJunction>{

  private final DoneTagsJunctionMapper doneTagsJunctionMapper;

  public DoneTagJunctionService(DoneTagsJunctionMapper mapper){
    super(mapper);
    this.doneTagsJunctionMapper = mapper;
  }

  @Override
  public List<Integer> searchTagIdsByActionId(int doneId) {
    return super.searchTagIdsByActionId(doneId);
  }

  @Override
  public void registerNewRelations(
      int actionId, BiFunction<Integer, Integer, DoneTagJunction> relationFactory,
      List<Integer> tagIdList) {

    super.registerNewRelations(actionId, relationFactory, tagIdList);
  }

  @Override
  @Transactional
  public void updateRelations(
      int actionId, BiFunction<Integer, Integer, DoneTagJunction> relationFactory,
      List<Integer> newTagIdList) {

    super.updateRelations(actionId, relationFactory, newTagIdList);
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
    doneTagsJunctionMapper.deleteRelationByDate(userId, date);
  }
}
