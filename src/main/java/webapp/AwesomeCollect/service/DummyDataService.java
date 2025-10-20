package webapp.AwesomeCollect.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.common.constant.CsvFileName;
import webapp.AwesomeCollect.common.util.CsvLoader;
import webapp.AwesomeCollect.dto.dummy.DummyArticleStockDto;
import webapp.AwesomeCollect.dto.dummy.DummyDoneDto;
import webapp.AwesomeCollect.dto.dummy.DummyGoalDto;
import webapp.AwesomeCollect.dto.dummy.DummyMemoDto;
import webapp.AwesomeCollect.dto.dummy.DummyTodoDto;
import webapp.AwesomeCollect.service.action.ArticleStockService;
import webapp.AwesomeCollect.service.action.DailyDoneService;
import webapp.AwesomeCollect.service.action.DailyTodoService;
import webapp.AwesomeCollect.service.action.GoalService;
import webapp.AwesomeCollect.service.action.MemoService;

/**
 * ダミーデータのサービスクラス。
 */
@Service
@RequiredArgsConstructor
public class DummyDataService {

  private final DailyTodoService dailyTodoService;
  private final DailyDoneService dailyDoneService;
  private final GoalService goalService;
  private final MemoService memoService;
  private final ArticleStockService articleStockService;

  /**
   * CSVファイルからダミーデータを読み込み、エンティティに変換してDBに登録する。
   *
   * @param guestUserId ゲストユーザーID
   */
  public void registerDummyData(int guestUserId){
    injectDummyTodo(guestUserId);
    injectDummyDone(guestUserId);
    injectDummyGoal(guestUserId);
    injectDummyMemo(guestUserId);
    injectDummyArticleStock(guestUserId);
  }

  private void injectDummyTodo(int guestUserId) {
    InputStream inputStream;
    try {
      inputStream = new ClassPathResource(CsvFileName.DUMMY_TODO).getInputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    List<DummyTodoDto> recordList =
        CsvLoader.load(inputStream, DummyTodoDto :: fromCsvRecord);

    dailyTodoService.registerDummyTodo(guestUserId, recordList);
  }

  private void injectDummyDone(int guestUserId) {
    InputStream inputStream;
    try {
      inputStream = new ClassPathResource(CsvFileName.DUMMY_DONE).getInputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    List<DummyDoneDto> recordList =
        CsvLoader.load(inputStream, DummyDoneDto :: fromCsvRecord);

    dailyDoneService.registerDummyDone(guestUserId, recordList);
  }

  private void injectDummyGoal(int guestUserId) {
    InputStream inputStream;
    try {
      inputStream = new ClassPathResource(CsvFileName.DUMMY_GOAL).getInputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    List<DummyGoalDto> recordList =
        CsvLoader.load(inputStream, DummyGoalDto :: fromCsvRecord);

    goalService.registerDummyGoal(guestUserId, recordList);
  }

  private void injectDummyMemo(int guestUserId) {
    InputStream inputStream;
    try {
      inputStream = new ClassPathResource(CsvFileName.DUMMY_MEMO).getInputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    List<DummyMemoDto> recordList =
        CsvLoader.load(inputStream, DummyMemoDto :: fromCsvRecord);

    memoService.registerDummyMemo(guestUserId, recordList);
  }

  private void injectDummyArticleStock(int guestUserId){
    InputStream inputStream;
    try {
      inputStream = new ClassPathResource(CsvFileName.DUMMY_ARTICLE_STOCK).getInputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    List<DummyArticleStockDto> recordList =
        CsvLoader.load(inputStream, DummyArticleStockDto :: fromCsvRecord);

    articleStockService.registerDummyArticleStock(guestUserId, recordList);
  }
}
