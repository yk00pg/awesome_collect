package webapp.AwesomeCollect.dto.action.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import webapp.AwesomeCollect.common.util.LearningTimeConverter;
import webapp.AwesomeCollect.dto.action.response.TodoResponseDto;
import webapp.AwesomeCollect.entity.action.DailyDone;

/**
 * できたこと入力用データオブジェクト。
 */
@Data
public class DoneRequestDto {

  private static final String DATE_PATTERN = "yyyy-MM-dd";
  private static final int CONTENT_MAX_SIZE = 100;
  private static final int MIN_TIME = 0;
  private static final int MAX_HOURS = 24;
  private static final int MAX_MINUTES = 59;
  private static final int MEMO_MAX_SIZE = 500;

  private TodoResponseDto todoResponseDto;

  @NotNull(message = "{date.blank}")
  @DateTimeFormat(pattern = DATE_PATTERN)
  private LocalDate date;

  private List<Integer> idList = new ArrayList<>();

  private List<
      @Size(max = CONTENT_MAX_SIZE, message = "{contentList.size}")
          String> contentList = new ArrayList<>();

  private List<
      @Min(value = MIN_TIME, message = "{hours.range}")
      @Max(value = MAX_HOURS, message = "{hours.range}")
          Integer> hoursList = new ArrayList<>();

  private List<
      @Min(value = MIN_TIME, message = "{minutes.range}")
      @Max(value = MAX_MINUTES, message = "{minutes.range")
          Integer> minutesList = new ArrayList<>();

  private List<@Size(max = MEMO_MAX_SIZE, message = "{memo.size}") String> memoList =
      new ArrayList<>();

  private List<String> tagsList = new ArrayList<>();

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
  public DailyDone toDailyDoneForRegistration(int userId, int index){
    DailyDone dailyDone = new DailyDone();
    dailyDone.setUserId(userId);
    dailyDone.setDate(this.date);
    dailyDone.setContent(contentList.get(index).trim());
    dailyDone.setMinutes(
        LearningTimeConverter.toTotalMinutes(
            hoursList.get(index), minutesList.get(index)));
    dailyDone.setMemo(memoList.get(index).trim());
    dailyDone.setRegisteredAt(LocalDateTime.now());
    return dailyDone;
  }

  /**
   * 入力されたデータを更新用のエンティティに変換する。
   *
   * @param userId  ユーザーID
   * @param index インデックス番号
   * @return  更新用のエンティティ
   */
  public DailyDone toDailyDoneForUpdate(int userId, int index){
    DailyDone dailyDone = new DailyDone();
    dailyDone.setId(idList.get(index));
    dailyDone.setUserId(userId);
    dailyDone.setDate(this.date);
    dailyDone.setContent(contentList.get(index).trim());
    dailyDone.setMinutes(
        LearningTimeConverter.toTotalMinutes(
            hoursList.get(index), minutesList.get(index)));
    dailyDone.setMemo(memoList.get(index).trim());
    dailyDone.setUpdatedAt(LocalDateTime.now());
    return dailyDone;
  }

  /**
   * DBから取得したできたことリストを入力用データオブジェクトに変換する。
   *
   * @param doneList  できたことリスト
   * @return  入力用データオブジェクト
   */
  public static DoneRequestDto fromDailyDone(List<DailyDone> doneList){
    DoneRequestDto dto = new DoneRequestDto();
    dto.date = doneList.getFirst().getDate();
    doneList.forEach(done -> {
      dto.idList.add(done.getId());
      dto.contentList.add(done.getContent());
      dto.hoursList.add(
          LearningTimeConverter.toHoursPart(done.getMinutes()));
      dto.minutesList.add(
          LearningTimeConverter.toMinutesPart(done.getMinutes()));
      dto.memoList.add(String.valueOf(done.getMemo()));
      dto.deletableList.add(Boolean.FALSE);
    });
    return dto;
  }

  /**
   * 日付、id(0)、時間(0)、分(0)以外空欄の入力用データオブジェクトを作成する。
   *
   * @param date  日付
   * @return  入力用データオブジェクト
   */
  public static DoneRequestDto createBlankDto(LocalDate date){
    DoneRequestDto dto = new DoneRequestDto();
    dto.date = date;
    dto.idList.addFirst(0);
    dto.hoursList.add(0);
    dto.minutesList.add(0);
    return dto;
  }
}
