package webapp.AwesomeCollect.service.action;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.mapper.action.DailyDoneMapper;
import webapp.AwesomeCollect.entity.action.DailyDone;

@Service
public class DailyDoneService {

  private final DailyDoneMapper mapper;

  public DailyDoneService(DailyDoneMapper mapper){
    this.mapper = mapper;
  }

  public List<DailyDone> searchDailyDone(int userId, LocalDate date){
    return mapper.selectDailyDone(userId, date);
  }

  public int countDailyDone(int userId){
    return mapper.countDailyDone(userId);
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
