package com.awesomecollect.service.action;

import com.awesomecollect.controller.web.SessionManager;
import com.awesomecollect.dto.action.request.TodoRequestDto;
import com.awesomecollect.dto.action.response.TodoResponseDto;
import com.awesomecollect.dto.dummy.DummyTodoDto;
import com.awesomecollect.entity.action.DailyTodo;
import com.awesomecollect.repository.action.DailyTodoRepository;
import com.awesomecollect.service.user.UserProgressService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * やることのサービスクラス。
 */
@Service
public class DailyTodoService {

  private final DailyTodoRepository dailyTodoRepository;
  private final UserProgressService userProgressService;
  private final SessionManager sessionManger;

  public DailyTodoService(
      DailyTodoRepository dailyTodoRepository, UserProgressService userProgressService,
      SessionManager sessionManger) {

    this.dailyTodoRepository = dailyTodoRepository;
    this.userProgressService = userProgressService;
    this.sessionManger = sessionManger;
  }

  /**
   * ユーザーIDと日付を基にDBを確認し、やることが登録されていない場合は空の表示用データオブジェクトを、
   * 登録されている場合は登録データを詰めた表示用データオブジェクトを用意する。
   *
   * @param userId ユーザーID
   * @param date   日付
   * @return やること表示用データオブジェクト
   */
  public TodoResponseDto prepareResponseDto(int userId, LocalDate date) {
    List<DailyTodo> dailyTodoList = dailyTodoRepository.searchDailyTodo(userId, date);

    if (dailyTodoList == null || dailyTodoList.isEmpty()) {
      return TodoResponseDto.createBlankDto(date);
    } else {
      return TodoResponseDto.fromDailyTodo(dailyTodoList);
    }
  }

  /**
   * ユーザーIDと日付を基にDBを確認し、やることが登録されていない場合は空の入力用データオブジェクトを、
   * 登録されている場合は登録データを詰めた入力用データオブジェクトを用意する。
   *
   * @param userId ユーザーID
   * @param date   日付
   * @return やること入力用データオブジェクト
   */
  public TodoRequestDto prepareRequestDto(int userId, LocalDate date) {
    List<DailyTodo> dailyTodoList = dailyTodoRepository.searchDailyTodo(userId, date);

    if (dailyTodoList == null || dailyTodoList.isEmpty()) {
      return TodoRequestDto.createBlankDto(date);
    } else {
      return TodoRequestDto.fromDailyTodo(dailyTodoList);
    }
  }

  /**
   * データの種類を判別してDBに保存（登録・更新・削除、内容が空の場合はスキップ）し、
   * セッションのレコード数更新情報を変更する。
   *
   * @param userId ユーザーID
   * @param dto    やること入力用データオブジェクト
   */
  @Transactional
  public void saveDailyTodo(int userId, TodoRequestDto dto) {
    for (int i = 0; i < dto.getContentList().size(); i++) {
      String content = dto.getContentList().get(i);
      if (content.isBlank()) {
        continue;
      }

      // 可変行のID=0が効かないケースに備えてnullを回避
      int todoId =
          dto.getIdList().get(i) == null
              ? 0
              : dto.getIdList().get(i);

      if (todoId == 0) {
        registerDailyTodo(userId, dto, i);
      } else {
        if (dto.isDeletable(i)) {
          dailyTodoRepository.deleteDailyTodoById(todoId);
        } else {
          dailyTodoRepository.updateDailyTodo(dto.toDailyTodoForUpdate(userId, i));
        }
      }
    }

    sessionManger.setHasUpdatedRecordCount(true);
  }

  /**
   * DTOをエンティティに変換してDBに登録し、日ごとの初回登録時の場合はユーザーの進捗情報も併せて変更する。
   *
   * @param userId ユーザーID
   * @param dto    やること入力用データオブジェクト
   * @param index  リストのインデックス番号
   */
  private void registerDailyTodo(int userId, TodoRequestDto dto, int index) {
    DailyTodo dailyTodo = dto.toDailyTodoForRegistration(userId, index);
    dailyTodoRepository.registerDailyTodo(dailyTodo);

    if (index == 0) {
      userProgressService.updateUserProgress(userId);
    }
  }

  /**
   * 指定の日付のやることをすべて削除し、セッションのレコード数更新情報を変更する。
   *
   * @param userId ユーザーID
   * @param date   日付
   */
  public void deleteDailyAllTodo(int userId, LocalDate date) {
    dailyTodoRepository.deleteDailyTodoByDate(userId, date);
    sessionManger.setHasUpdatedRecordCount(true);
  }

  /**
   * CSVファイルから読み込んだダミーデータをDBに登録し、セッションのレコード数更新情報を変更する。
   *
   * @param guestUserId ゲストユーザーID
   * @param recordList  CSVファイルから読み込んだレコードリスト
   */
  public void registerDummyTodo(int guestUserId, List<DummyTodoDto> recordList){
    LocalDate referenceDate = LocalDate.now();
    for (int i = 0; i < recordList.size(); i++) {
      LocalDate date = referenceDate;
      DummyTodoDto dto = recordList.get(i);
      if(i == 0 || (!dto.getDate().equals(recordList.get(i - 1).getDate()))){
        date = referenceDate.minusDays(1);
      }

      DailyTodo dailyTodo = dto.toEntity(guestUserId, date);
      dailyTodoRepository.registerDailyTodo(dailyTodo);

      referenceDate = date;
    }

    sessionManger.setHasUpdatedRecordCount(true);
  }
}