package webapp.AwesomeCollect.dto.action;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import webapp.AwesomeCollect.entity.action.DailyDone;

@Data
public class DoneRequestDto {

  private static final String DATE_PATTERN = "yyyy-MM-dd";
  private static final int CONTENT_MAX_SIZE = 100;
  private static final String HOURS_PATTERN = "^$|^\\d{1,2}(\\.\\d{1,2})?$";
  private static final int MEMO_MAX_SIZE = 500;

  private TodoResponseDto todoResponseDto;

  @NotNull(message = "{date.blank}")
  @DateTimeFormat(pattern = DATE_PATTERN)
  private LocalDate date;

  private List<Integer> idList = new ArrayList<>();

  private List<@Size(max = CONTENT_MAX_SIZE, message = "{contentList.size}") String> contentList =
      new ArrayList<>();

  private List<@Pattern(regexp = HOURS_PATTERN) String> hoursList = new ArrayList<>();

  private List<@Size(max = MEMO_MAX_SIZE, message = "{memo.size}") String> memoList =
      new ArrayList<>();

  private List<String> tagsList = new ArrayList<>();

  private List<Boolean> deletableList = new ArrayList<>();

  // 削除チェックが入っているか確認
  public boolean isDeletable(int index){
    return Boolean.TRUE.equals(deletableList.get(index));
  }

  // 新規登録用のデータを詰めたエンティティに変換
  public DailyDone toDailyDone(int userId, int index){
    DailyDone dailyDone = new DailyDone();
    dailyDone.setUserId(userId);
    dailyDone.setDate(this.date);
    dailyDone.setContent(contentList.get(index).trim());
    dailyDone.setHours(new BigDecimal(hoursList.get(index)));
    dailyDone.setMemo(memoList.get(index).trim());
    dailyDone.setRegisteredAt(LocalDateTime.now());
    return dailyDone;
  }

  /**
   * データオブジェクトをID付きでエンティティに変換する。
   * 
   * @param i　できたことのインデックス
   * @param userId  ユーザーID
   * @return  変換後のエンティティ（ID付き）
   */
  public DailyDone toDailyDoneWithId(int i, int userId){
    DailyDone dailyDone = new DailyDone();
    dailyDone.setId(idList.get(i));
    dailyDone.setUserId(userId);
    dailyDone.setDate(this.date);
    dailyDone.setContent(contentList.get(i).trim());
    dailyDone.setHours(new BigDecimal(hoursList.get(i)));
    dailyDone.setMemo(memoList.get(i).trim());
    dailyDone.setUpdatedAt(LocalDateTime.now());
    return dailyDone;
  }

  // DBから取得したできたことリストをデータオブジェクトに変換
  public static DoneRequestDto fromDailyDone(List<DailyDone> doneList){
    DoneRequestDto dto = new DoneRequestDto();
    dto.date = doneList.getFirst().getDate();
    doneList.forEach(done -> {
      dto.idList.add(done.getId());
      dto.contentList.add(done.getContent());
      dto.hoursList.add(String.valueOf(done.getHours()));
      dto.memoList.add(String.valueOf(done.getMemo()));
      dto.deletableList.add(Boolean.FALSE);
    });
    return dto;
  }

  // 日付とid=0を詰めたデータオブジェクトを作成
  public static DoneRequestDto createBlankDto(LocalDate date){
    DoneRequestDto dto = new DoneRequestDto();
    dto.date = date;
    dto.idList.addFirst(0);
    return dto;
  }
}
