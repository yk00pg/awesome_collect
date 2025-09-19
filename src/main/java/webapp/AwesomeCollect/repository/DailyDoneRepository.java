package webapp.AwesomeCollect.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.entity.action.DailyDone;
import webapp.AwesomeCollect.mapper.action.DailyDoneMapper;

/**
 * できたことのリポジトリクラス。
 */
@Repository
public class DailyDoneRepository {

  private final DailyDoneMapper mapper;

  public DailyDoneRepository(DailyDoneMapper mapper){
    this.mapper = mapper;
  }

  public List<DailyDone> searchDailyDone(int userId, LocalDate date){
    return mapper.selectDailyDone(userId, date);
  }

  public void registerDailyDone(DailyDone done){
    mapper.insertDone(done);
  }

  public void updateDailyDone(DailyDone done){
    mapper.updateDone(done);
  }

  public void deleteDailyDoneById(int id){
    mapper.deleteDoneById(id);
  }

  public void deleteDailyDoneByDate(int userId, LocalDate date){
    mapper.deleteDoneByDate(userId, date);
  }
}
