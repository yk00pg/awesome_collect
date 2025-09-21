package webapp.AwesomeCollect.service.action;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.common.SessionManager;
import webapp.AwesomeCollect.dto.action.request.TodoRequestDto;
import webapp.AwesomeCollect.dto.action.response.TodoResponseDto;
import webapp.AwesomeCollect.entity.action.DailyTodo;
import webapp.AwesomeCollect.repository.action.DailyTodoRepository;
import webapp.AwesomeCollect.service.user.UserProgressService;

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
   * DBにやることが登録されていない場合は空の表示用データオブジェクトを、<br>
   * 登録されている場合は登録データを詰めた表示用データオブジェクトを用意する。
   *
   * @param userId  ユーザーID
   * @param date  日付
   * @return  やること表示用データオブジェクト
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
   * DBにやることが登録されていない場合は空の入力用データオブジェクトを、<br>
   * 登録されている場合は登録データを詰めた入力用データオブジェクトを用意する。
   *
   * @param userId  ユーザーID
   * @param date  日付
   * @return  やること入力用データオブジェクト
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
   * データの種類を判別してDBに保存（登録・更新・削除）する。<br>
   * 内容が空の場合は何もせずスキップする。
   *
   * @param userId  ユーザーID
   * @param dto やること入力用データオブジェクト
   */
  @Transactional
  public void saveDailyTodo(int userId, TodoRequestDto dto) {
    for (int i = 0; i < dto.getContentList().size(); i++) {
      int id = dto.getIdList().get(i);
      String content = dto.getContentList().get(i);

      if(content == null || content.isBlank()){
        continue;
      }

      if (id == 0) {
        registerDailyTodo(userId, dto, i);
      } else {
        if (dto.isDeletable(i)) {
          dailyTodoRepository.deleteDailyTodoById(id);
        } else {
          dailyTodoRepository.updateDailyTodo(dto.toDailyTodoForUpdate(userId, i));
        }
      }
    }
  }

  /**
   * DTOをエンティティに変換してDBに登録し、セッションのレコード数更新情報を変更する。<br>
   * 日ごとの初回登録時のみユーザーの進捗情報を更新する。
   *
   * @param userId  ユーザーID
   * @param dto やること入力用データオブジェクト
   * @param index リストのインデックス番号
   */
  private void registerDailyTodo(int userId, TodoRequestDto dto, int index) {
    DailyTodo dailyTodo = dto.toDailyTodoForRegistration(userId, index);
    dailyTodoRepository.registerDailyTodo(dailyTodo);
    sessionManger.setHasUpdatedRecordCount(true);
    if(index == 0){
      userProgressService.updateUserProgress(userId);
    }
  }

  /**
   * 指定の日付のやることをすべて削除し、セッションのレコード数更新情報を変更する。
   *
   * @param userId  ユーザーID
   * @param date  日付
   */
  public void deleteDailyAllTodo(int userId, LocalDate date){
    dailyTodoRepository.deleteDailyTodoByDate(userId, date);
    sessionManger.setHasUpdatedRecordCount(true);
  }
}