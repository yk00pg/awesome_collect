package webapp.AwesomeCollect.dto.action;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import webapp.AwesomeCollect.entity.action.DailyTodo;

/**
 * やることの入力用データオブジェクト。
 */
@Data
public class TodoRequestDto {

  private static final String DATE_PATTERN = "yyyy-MM-dd";
  private static final int CONTENT_MAX_SIZE = 100;

  @NotNull(message = "{date.blank}")
  @DateTimeFormat(pattern = DATE_PATTERN)
  private LocalDate date;

  private List<Integer> idList = new ArrayList<>();

  private List<
      @Size(max = CONTENT_MAX_SIZE,
      message = "{contentList.size}") String> contentList = new ArrayList<>();

  private List<Boolean> deletableList = new ArrayList<>();

  /**
   * 削除チェックが入っているかどうか判定する。
   *
   * @param index インデックス番号
   * @return  削除チェックが入っているかどうか
   */
  public boolean isDeletable(int index){
    return Boolean.TRUE.equals(deletableList.get(index));
  }

  /**
   * 入力されたデータを新規登録用のエンティティに変換する。
   *
   * @param userId  ユーザーID
   * @param index インデックス番号
   * @return  新規登録用のエンティティ
   */
  public DailyTodo toDailyTodoForRegistration(int userId, int index){
    DailyTodo dailyTodo = new DailyTodo();
    dailyTodo.setUserId(userId);
    dailyTodo.setDate(date);
    dailyTodo.setContent(contentList.get(index));
    dailyTodo.setRegisteredAt(LocalDateTime.now());
    return dailyTodo;
  }

   /**
   * 入力されたデータを更新用のエンティティに変換する。
   *
   * @param userId  ユーザーID
   * @param index インデックス番号
   * @return  更新用のエンティティ
   */
  public DailyTodo toDailyTodoForUpdate(int userId, int index){
    DailyTodo dailyTodo = new DailyTodo();
    dailyTodo.setId(idList.get(index));
    dailyTodo.setUserId(userId);
    dailyTodo.setDate(date);
    dailyTodo.setContent(contentList.get(index));
    dailyTodo.setUpdatedAt(LocalDateTime.now());
    return dailyTodo;
  }

  /**
   * DBから取得したやることリストをデータオブジェクトに変換する。
   *
   * @param todoList  やることリスト
   * @return  データオブジェクト
   */
  public static TodoRequestDto fromDailyTodo (List<DailyTodo> todoList){
    TodoRequestDto dto = new TodoRequestDto();
    dto.date = todoList.getFirst().getDate();
    todoList.forEach(todo -> {
      dto.idList.add(todo.getId());
      dto.contentList.add(todo.getContent());
      dto.deletableList.add(Boolean.FALSE);
    });
    return dto;
  }

  /**
   * 日付とid(0)以外空欄のデータオブジェクトを作成する。
   *
   * @param date  日付
   * @return  データオブジェクト
   */
  public static TodoRequestDto createBlankDto(LocalDate date){
    TodoRequestDto dto = new TodoRequestDto();
    dto.date = date;
    dto.idList.addFirst(0);
    return dto;
  }
}
