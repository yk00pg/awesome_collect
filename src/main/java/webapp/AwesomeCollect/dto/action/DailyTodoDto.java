package webapp.AwesomeCollect.dto.action;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import webapp.AwesomeCollect.common.util.DateTimeFormatUtil;
import webapp.AwesomeCollect.entity.action.DailyTodo;

@Data
public class DailyTodoDto {

  private List<Integer> idList = new ArrayList<>(List.of(0,0,0));

  @NotNull(message = "日付を入力してください")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

  private List<@Size(max = 100, message = "内容は100字以内で入力してください") String> contentList =
      new ArrayList<>(List.of("","",""));

  private String registeredAt;
  private String updatedAt;

  public boolean isContentEmpty(int i){
    return contentList.get(i) == null || contentList.get(i).trim().isEmpty();
  }

  public DailyTodo toDailyTodo(int i, int userId){
    DailyTodo dailyTodo = new DailyTodo();
    dailyTodo.setUserId(userId);
    dailyTodo.setDate(this.date);
    dailyTodo.setContent(contentList.get(i));
    dailyTodo.setRegisteredAt(LocalDateTime.now());
    return dailyTodo;
  }

  public DailyTodo toDailyTodoWithId(int i, int userId){
    DailyTodo dailyTodo = new DailyTodo();
    dailyTodo.setId(idList.get(i));
    dailyTodo.setUserId(userId);
    dailyTodo.setDate(this.date);
    dailyTodo.setContent(contentList.get(i));
    dailyTodo.setUpdatedAt(LocalDateTime.now());
    return dailyTodo;
  }

  public DailyTodoDto fromDailyTodo(List<DailyTodo> todoList){
    DailyTodoDto dto = new DailyTodoDto();

    dto.idList = new ArrayList<>();
    dto.contentList = new ArrayList<>();

    if(!todoList.isEmpty()){
      dto.date = todoList.getFirst().getDate();

      for (DailyTodo todo : todoList) {
        dto.idList.add(todo.getId());
        dto.contentList.add(todo.getContent());
      }
    }

    dto.registeredAt = todoList.stream()
        .map(DailyTodo :: getRegisteredAt)
        .min(Comparator.naturalOrder())
        .map(DateTimeFormatUtil :: formatDateTime)
        .get();

    dto.updatedAt = todoList.stream()
        .map(DailyTodo :: getUpdatedAt)
        .filter(Objects :: nonNull)
        .max(Comparator.naturalOrder())
        .map(DateTimeFormatUtil :: formatDateTime)
        .orElse(null);

    while(dto.contentList.size() < 3){
      dto.idList.add(0);
      dto.contentList.add("");
    }
    return dto;
  }
}
