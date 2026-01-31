package webapp.AwesomeCollect.dto.action.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import webapp.AwesomeCollect.common.util.DateTimeFormatUtil;
import webapp.AwesomeCollect.entity.action.DailyTodo;

/**
 * やること表示用データオブジェクト。
 */
@Data
public class TodoResponseDto {

  private LocalDate date;
  private List<String> contentList = new ArrayList<>();
  private String registeredAt;
  private String updatedAt;

  private String formattedDate;
  private LocalDate prevDate;
  private LocalDate nextDate;
  private boolean hasContent;

  /**
   * DBから取得したやることリストを表示用データオブジェクトに変換する。
   *
   * @param todoList  やることリスト
   * @return  表示用データオブジェクト
   */
  public static TodoResponseDto fromDailyTodo (List<DailyTodo> todoList){
    TodoResponseDto dto = new TodoResponseDto();

    dto.date = todoList.getFirst().getDate();
    todoList.forEach(todo -> dto.contentList.add(todo.getContent()));

    // 初回登録日を設定
    // noinspection OptionalGetWithoutIsPresent
    dto.registeredAt = todoList.stream()
        .map(DailyTodo::getRegisteredAt)
        .min(Comparator.naturalOrder())
        .map(DateTimeFormatUtil::formatDateTime)
        .get();

    // 最終更新日を設定
    dto.updatedAt = todoList.stream()
        .map(DailyTodo::getUpdatedAt)
        .filter(Objects::nonNull)
        .max(Comparator.naturalOrder())
        .map(DateTimeFormatUtil::formatDateTime)
        .orElse(null);

    dto.formattedDate = DateTimeFormatUtil.formatDate(dto.date);
    dto.prevDate = dto.date.minusDays(1);
    dto.nextDate = dto.date.plusDays(1);
    dto.hasContent = true;

    return dto;
  }

  /**
   * 日付以外空欄のデータオブジェクトを作成する。
   *
   * @param date  日付
   * @return  データオブジェクト
   */
  public static TodoResponseDto createBlankDto(LocalDate date){
    TodoResponseDto dto = new TodoResponseDto();
    dto.date = date;
    dto.formattedDate = DateTimeFormatUtil.formatDate(date);
    dto.prevDate = date.minusDays(1);
    dto.nextDate = date.plusDays(1);
    dto.hasContent = false;
    return dto;
  }
}
