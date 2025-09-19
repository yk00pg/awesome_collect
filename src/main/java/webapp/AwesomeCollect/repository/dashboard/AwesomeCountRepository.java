package webapp.AwesomeCollect.repository.dashboard;

import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.mapper.dashboard.AwesomeCountMapper;

/**
 * えらいポイントのリポジトリクラス。
 */
@Repository
public class AwesomeCountRepository {

  private final AwesomeCountMapper mapper;

  public AwesomeCountRepository(AwesomeCountMapper mapper){
    this.mapper = mapper;
  }

  public int countDailyTodoRecord(int userId){
    return mapper.countDailyTodo(userId);
  }

  public int countDailyDoneRecord(int userId){
    return mapper.countDailyDone(userId);
  }

  public int countGoalRecord(int userId){
    return mapper.countGoal(userId);
  }

  public int countAchievedGoalRecord(int userId){
    return mapper.countAchieved(userId);
  }

  public int countMemoRecord(int userId){
    return mapper.countMemo(userId);
  }

  public int countArticleStockRecord(int userId){
    return mapper.countArticleStock(userId);
  }

  public int countFinishedArticleRecord(int userId){
    return mapper.countFinished(userId);
  }

}
