package webapp.AwesomeCollect.dto.dummy;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.apache.commons.csv.CSVRecord;
import webapp.AwesomeCollect.common.constant.CsvHeader;
import webapp.AwesomeCollect.entity.action.Goal;

@Data
public class DummyGoalDto {

  private static final String ACHIEVED = "achieved";

  private String title;
  private String content;
  private String status;
  private List<String> tagList;

  public Goal toEntity(int guestUserId){
    Goal goal = new Goal();
    goal.setUserId(guestUserId);
    goal.setTitle(title);
    goal.setContent(content);
    goal.setAchieved(status.equals(ACHIEVED));
    goal.setRegisteredAt(LocalDateTime.now());
    return goal;
  }

  public static DummyGoalDto fromCsvRecord(CSVRecord record){
    DummyGoalDto dto = new DummyGoalDto();
    dto.title = record.get(CsvHeader.TITLE);
    dto.content = record.get(CsvHeader.CONTENT);
    dto.status = record.get(CsvHeader.STATUS);

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
