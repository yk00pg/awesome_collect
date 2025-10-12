package webapp.AwesomeCollect.dto.dummy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import org.apache.commons.csv.CSVRecord;
import webapp.AwesomeCollect.common.constant.CsvHeader;
import webapp.AwesomeCollect.entity.action.DailyTodo;

@Data
public class DummyTodoDto {

  private LocalDate date;
  private String content;

  public static DummyTodoDto fromCsvRecord(CSVRecord record){
    DummyTodoDto dto = new DummyTodoDto();
    dto.date = LocalDate.parse(record.get(CsvHeader.DATE));
    dto.content = record.get(CsvHeader.CONTENT);
    return dto;
  }

  public DailyTodo toEntity(int guestUserId, LocalDate date){
    DailyTodo dailyTodo = new DailyTodo();
    dailyTodo.setUserId(guestUserId);
    dailyTodo.setDate(date);
    dailyTodo.setContent(content);
    dailyTodo.setRegisteredAt(LocalDateTime.now());
    return dailyTodo;
  }

}
