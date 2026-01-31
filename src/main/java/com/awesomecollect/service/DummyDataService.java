package com.awesomecollect.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.awesomecollect.common.constant.CsvFileName;
import com.awesomecollect.common.util.CsvLoader;
import com.awesomecollect.dto.dummy.DummyArticleStockDto;
import com.awesomecollect.dto.dummy.DummyDoneDto;
import com.awesomecollect.dto.dummy.DummyGoalDto;
import com.awesomecollect.dto.dummy.DummyMemoDto;
import com.awesomecollect.dto.dummy.DummyTodoDto;
import com.awesomecollect.service.action.ArticleStockService;
import com.awesomecollect.service.action.DailyDoneService;
import com.awesomecollect.service.action.DailyTodoService;
import com.awesomecollect.service.action.GoalService;
import com.awesomecollect.service.action.MemoService;

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
