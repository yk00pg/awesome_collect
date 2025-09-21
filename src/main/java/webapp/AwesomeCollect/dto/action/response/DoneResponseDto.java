package webapp.AwesomeCollect.dto.action.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Data;
import webapp.AwesomeCollect.common.util.DateTimeFormatUtil;
import webapp.AwesomeCollect.common.util.LearningTimeConverter;
import webapp.AwesomeCollect.entity.action.DailyDone;

/**
 * できたこと表示用データオブジェクト。
 */
@Data
public class DoneResponseDto {

  private LocalDate date;
  private List<String> contentList = new ArrayList<>();
  private List<String> learningTimeList = new ArrayList<>();
  private List<String> memoList = new ArrayList<>();
  private List<List<String>> tagsList = new ArrayList<>();

  private String registeredAt;
  private String updatedAt;

  private String totalLearningTime;

  private String formattedDate;
  private LocalDate prevDate;
  private LocalDate nextDate;
  private boolean hasContent;

  /**
   * DBから取得したできたことリストを表示用データオブジェクトに変換する。
   *
   * @param doneList  できたことリスト
   * @return  表示用データオブジェクト
   */
  public static DoneResponseDto fromDailyDone(List<DailyDone> doneList){
    DoneResponseDto dto = new DoneResponseDto();
    dto.date = doneList.getFirst().getDate();

    AtomicInteger totalMinute = new AtomicInteger();
    doneList.forEach(done -> {
      dto.contentList.add(done.getContent());
      dto.learningTimeList.add(
          LearningTimeConverter.formatAsJpString(done.getMinutes()));
      dto.memoList.add(done.getMemo());
      dto.tagsList.add(Collections.emptyList());
      totalMinute.addAndGet(done.getMinutes());
    });

    // 初回登録日を設定
    // noinspection OptionalGetWithoutIsPresent
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

    dto.totalLearningTime =
        LearningTimeConverter.formatAsJpString(totalMinute.intValue());

    dto.formattedDate = DateTimeFormatUtil.formatDate(dto.date);
    dto.prevDate = dto.date.minusDays(1);
    dto.nextDate = dto.date.plusDays(1);
    dto.hasContent = true;

    return dto;
  }

  /**
   * 日付以外空欄の表示用データオブジェクトを作成する。
   *
   * @param date  日付
   * @return  表示用データオブジェクト
   */
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
