package webapp.AwesomeCollect.service.action;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.common.SessionManager;
import webapp.AwesomeCollect.dto.action.TodoRequestDto;
import webapp.AwesomeCollect.dto.action.TodoResponseDto;
import webapp.AwesomeCollect.entity.action.DailyTodo;
import webapp.AwesomeCollect.repository.DailyTodoRepository;
import webapp.AwesomeCollect.service.UserProgressService;

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

  // DBの登録状況に応じた閲覧用データオブジェクトを返す
  public TodoResponseDto prepareResponseDto(int userId, LocalDate date) {
    List<DailyTodo> dailyTodoList = dailyTodoRepository.searchDailyTodo(userId, date);

    if (dailyTodoList == null || dailyTodoList.isEmpty()) {
      return TodoResponseDto.createBlankDto(date);
    } else {
      return TodoResponseDto.fromDailyTodo(dailyTodoList);
    }
  }

  // DBの登録状況に応じた編集用データオブジェクトを返す
  public TodoRequestDto prepareRequestDto(int userId, LocalDate date) {
    List<DailyTodo> dailyTodoList = dailyTodoRepository.searchDailyTodo(userId, date);

    if (dailyTodoList == null || dailyTodoList.isEmpty()) {
      return TodoRequestDto.createBlankDto(date);
    } else {
      return TodoRequestDto.fromDailyTodo(dailyTodoList);
    }
  }

  /**
   * データの種類に応じてDBへの保存処理（登録・削除・更新）を行う。
   *
   * @param userId  ユーザーID
   * @param dto やることのデータオブジェクト
   */
  @Transactional
  public void saveDailyTodo(int userId, TodoRequestDto dto) {
    for (int i = 0; i < dto.getContentList().size(); i++) {
      int id = dto.getIdList().get(i);
      String content = dto.getContentList().get(i);

      // 内容が空の場合はスキップ
      if(content == null || content.isBlank()){
        continue;
      }

      if (id == 0) {
        registerTodo(userId, dto, i);
      } else {
        // 削除チェックが入っているか確認
        if (dto.isDeletable(i)) {
          dailyTodoRepository.deleteDailyTodoById(id);
        } else {
          DailyTodo dailyTodo = dto.toDailyTodoWithId(userId, i);
          dailyTodoRepository.updateDailyTodo(dailyTodo);
        }
      }
    }
  }

  /**
   * DTOをエンティティに変換してDBに登録し、セッション情報を変更する。<br>
   * 初回登録時のみユーザーの進捗情報を更新する。
   *
   * @param userId  ユーザーID
   * @param dto やることのデータオブジェクト
   * @param index リストのインデックス番号
   */
  private void registerTodo(int userId, TodoRequestDto dto, int index) {
    DailyTodo dailyTodo = dto.toDailyTodo(userId, index);
    dailyTodoRepository.registerDailyTodo(dailyTodo);
    sessionManger.setHasUpdatedRecordCount(true);
    if(index == 0){
      userProgressService.updateUserProgress(userId);
    }
  }

  // 指定の日付のやることをすべて削除
  public void deleteDailyAllTodo(int userId, LocalDate date){
    dailyTodoRepository.deleteDailyTodoByDate(userId, date);
    sessionManger.setHasUpdatedRecordCount(true);
  }

  // 指定のユーザーのレコード数を取得
  public Integer countDailyTodo(int userId){
    return dailyTodoRepository.countDailyTodo(userId);
  }
}