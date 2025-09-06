package webapp.AwesomeCollect.dto.action;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import webapp.AwesomeCollect.common.util.DateTimeFormatUtil;
import webapp.AwesomeCollect.entity.action.DailyDone;

@Data
public class DoneResponseDto {

  private LocalDate date;
  private List<String> contentList = new ArrayList<>();
  private List<String> hoursList = new ArrayList<>();
  private List<String> memoList = new ArrayList<>();
  private List<List<String>> tagsList = new ArrayList<>();

  private String registeredAt;
  private String updatedAt;

  private BigDecimal totalHours;

  private String formattedDate;
  private LocalDate prevDate;
  private LocalDate nextDate;
  private boolean hasContent;

  public static DoneResponseDto fromDailyDone(
      List<DailyDone> doneList, List<List<String>> tagNamesList){
    DoneResponseDto dto = new DoneResponseDto();
    dto.date = doneList.getFirst().getDate();
    doneList.forEach(done -> {
      dto.contentList.add(done.getContent());
      dto.hoursList.add(String.valueOf(done.getHours()));
      dto.memoList.add(done.getMemo());
      dto.tagsList.add(Collections.emptyList());
    });

    dto.tagsList = tagNamesList;

    // 初回登録日を設定
    dto.registeredAt = doneList.stream()
        .map(DailyDone :: getRegisteredAt)
        .min(Comparator.naturalOrder())
        .map(DateTimeFormatUtil :: formatDateTime)
        .get();

    // 最終更新日を設定
    dto.updatedAt = doneList.stream()
        .map(DailyDone :: getUpdatedAt)
        .filter(Objects :: nonNull)
        .max(Comparator.naturalOrder())
        .map(DateTimeFormatUtil :: formatDateTime)
        .orElse(null);

    // 1日の合計学習時間を設定
    BigDecimal totalHours = BigDecimal.ZERO;
    for(String hourStr : dto.hoursList){
      totalHours = totalHours.add(new BigDecimal(hourStr));
    }
    dto.totalHours = totalHours;

    dto.formattedDate = DateTimeFormatUtil.formatDate(dto.date);
    dto.prevDate = dto.date.minusDays(1);
    dto.nextDate = dto.date.plusDays(1);
    dto.hasContent = true;
    return dto;
  }

  // 日付以外空欄のDTOを作成して返す
  public static DoneResponseDto createBlankDto(LocalDate date){
    DoneResponseDto dto = new DoneResponseDto();
    dto.date = date;
    dto.formattedDate = DateTimeFormatUtil.formatDate(date);
    dto.prevDate = date.minusDays(1);
    dto.nextDate = date.plusDays(1);
    dto.hasContent = false;
    return dto;
  }
}
