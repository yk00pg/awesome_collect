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

  private List<@Size(max = CONTENT_MAX_SIZE, message = "{contentList.size}") String> contentList
      = new ArrayList<>();

  private List<Boolean> deletableList = new ArrayList<>();

  // 内容がすべて空か確認
  public boolean isContentListEmpty() {
    return contentList == null ||
        contentList.stream()
            .allMatch(content -> content == null || content.isBlank());
  }

  // 削除チェックが入っているか確認
  public boolean isDeletable(int index){
    return Boolean.TRUE.equals(deletableList.get(index));
  }

  // 新規登録用のデータを詰めたエンティティに変換
  public DailyTodo toDailyTodo(int userId, int index){
    DailyTodo dailyTodo = new DailyTodo();
    dailyTodo.setUserId(userId);
    dailyTodo.setDate(date);
    dailyTodo.setContent(contentList.get(index));
    dailyTodo.setRegisteredAt(LocalDateTime.now());
    return dailyTodo;
  }

  // 更新用のデータを詰めたエンティティに変換
  public DailyTodo toDailyTodoWithId(int userId, int index){
    DailyTodo dailyTodo = new DailyTodo();
    dailyTodo.setId(idList.get(index));
    dailyTodo.setUserId(userId);
    dailyTodo.setDate(date);
    dailyTodo.setContent(contentList.get(index));
    dailyTodo.setUpdatedAt(LocalDateTime.now());
    return dailyTodo;
  }

  // DBから取得したやることリストをデータオブジェクトに変換
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

  // 日付とid=0を詰めたデータオブジェクトを作成
  public static TodoRequestDto createBlankDto(LocalDate date){
    TodoRequestDto dto = new TodoRequestDto();
    dto.date = date;
    dto.idList.addFirst(0);
    return dto;
  }
}
