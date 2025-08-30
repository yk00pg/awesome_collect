package webapp.AwesomeCollect.service.junction;

import java.time.LocalDate;
import java.util.List;
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
    doneTagsJunctionMapper.deleteRelationByDate(userId, date);
  }
}
