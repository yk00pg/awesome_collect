package com.awesomecollect.repository.action;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.awesomecollect.entity.action.DailyDone;
import com.awesomecollect.mapper.action.DailyDoneMapper;

/**
 * できたことのリポジトリクラス。
 */
@Repository
public class DailyDoneRepository {

  private final DailyDoneMapper mapper;

  public DailyDoneRepository(DailyDoneMapper mapper) {
    this.mapper = mapper;
  }

  public List<Integer> searchIdByUserId(int userId){
    return mapper.selectIdByUserId(userId);
  }

  public List<DailyDone> searchDailyDone(int userId, LocalDate date) {
    return mapper.selectDailyDone(userId, date);
  }

  public void registerDailyDone(DailyDone done) {
    mapper.insertDone(done);
  }

  public void updateDailyDone(DailyDone done) {
    mapper.updateDone(done);
  }

  public void deleteDailyDoneById(int doneId) {
    mapper.deleteDoneById(doneId);
  }

  public void deleteDailyDoneByDate(int userId, LocalDate date) {
    mapper.deleteDoneByDate(userId, date);
  }

  public void deleteAllDoneByUserId(int userId){
    mapper.deleteAllDoneByUserId(userId);
  }
}
