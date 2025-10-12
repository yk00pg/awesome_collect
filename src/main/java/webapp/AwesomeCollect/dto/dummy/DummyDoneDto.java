package webapp.AwesomeCollect.dto.dummy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.apache.commons.csv.CSVRecord;
import webapp.AwesomeCollect.common.constant.CsvHeader;
import webapp.AwesomeCollect.common.util.LearningTimeConverter;
import webapp.AwesomeCollect.entity.action.DailyDone;

@Data
public class DummyDoneDto {

  private LocalDate date;
  private String content;
  private int hours;
  private int minutes;
  private String memo;
  private List<String> tagList;

  public DailyDone toEntity(int guestUserId, LocalDate date){
    DailyDone dailyDone = new DailyDone();
    dailyDone.setUserId(guestUserId);
    dailyDone.setDate(date);
    dailyDone.setContent(content);
    dailyDone.setMinutes(LearningTimeConverter.toTotalMinutes(hours, minutes));
    dailyDone.setMemo(memo);
    dailyDone.setRegisteredAt(LocalDateTime.now());
    return dailyDone;
  }

  public static DummyDoneDto fromCsvRecord(CSVRecord record){
    DummyDoneDto dto = new DummyDoneDto();
    dto.date = LocalDate.parse(record.get(CsvHeader.DATE));
    dto.content = record.get(CsvHeader.CONTENT);
    dto.hours = Integer.parseInt(record.get(CsvHeader.HOURS));
    dto.minutes = Integer.parseInt(record.get(CsvHeader.MINUTES));
    dto.memo = record.get(CsvHeader.MEMO);

    String tagCell = record.get(CsvHeader.TAG);
    if(tagCell == null || tagCell.isBlank()){
      dto.tagList = null;
    }else
      dto.tagList = Arrays.stream(tagCell.split(","))
          .map(String :: trim)
          .toList();

    return dto;
  }
}
