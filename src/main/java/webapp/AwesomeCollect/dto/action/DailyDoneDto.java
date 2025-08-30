package webapp.AwesomeCollect.dto.action;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import webapp.AwesomeCollect.common.util.DateTimeFormatUtil;
import webapp.AwesomeCollect.entity.action.DailyDone;
import webapp.AwesomeCollect.entity.action.DailyTodo;

@Data
public class DailyDoneDto {

  private List<Integer> idList = new ArrayList<>(List.of(0,0,0));
  private static int DEFAULT_SIZE = 3;

  @NotNull(message = "日付を入力してください")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

  private List<@Size(max = 100, message = "内容は100字以内で入力してください") String> contentList =
      new ArrayList<>(List.of("","",""));

  private List<@Pattern(regexp = "^$|^\\d{1,2}(\\.\\d{1,2})?$") String> hoursList =
      new ArrayList<>(List.of("","",""));

  private List<@Size(max = 500, message = "メモは500字以内で入力してください") String> memoList =
      new ArrayList<>(List.of("","",""));

  private List<String> tagsList = new ArrayList<>(List.of("","",""));

  private BigDecimal totalHours;
  private String registeredAt;
  private String updatedAt;

  /**
   * 内容がひとつ以上入力されているか判定する。
   * 
   * @return  内容がひとつ以上入力されているかどうか。
   */
  public boolean hasAtLeastOneContent(){
    return contentList != null &&
        contentList.stream()
            .anyMatch(content -> content != null && !content.trim().isEmpty());
  }

  public BigDecimal getTotalHours(){
    BigDecimal totalHours = BigDecimal.ZERO;
    for(String hourStr : hoursList){
      if(hourStr != null && !hourStr.isBlank()){
        BigDecimal hours = new BigDecimal(hourStr);
        totalHours = totalHours.add(hours);
      }
    }
    return totalHours;
  }

  /**
   * 学習時間の合計が24時間以内かどうか判定する。
   * 
   * @return  学習時間の合計が24時間以内かどうか。
   */
  public boolean isTotalHoursValid(){
    return getTotalHours().compareTo(new BigDecimal("24.00")) <= 0;
  }

  public boolean isContentEmpty(int i){
    return contentList.get(i) == null || contentList.get(i).trim().isEmpty();
  }

  public boolean isFutureDate(){
    LocalDate today = LocalDate.now();
    return date.isAfter(today);
  }

  /**
   * データオブジェクトをエンティティに変換する。
   * 
   * @param i できたことのインデックス
   * @param userId  ユーザーID
   * @return  変換後のエンティティ
   */
  public DailyDone toDailyDone(int i, int userId){
    DailyDone dailyDone = new DailyDone();
    dailyDone.setUserId(userId);
    dailyDone.setDate(this.date);
    dailyDone.setContent(contentList.get(i).trim());
    dailyDone.setHours(new BigDecimal(hoursList.get(i)));
    dailyDone.setMemo(memoList.get(i).trim());
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

  /**
   * エンティティをデータオブジェクトに変換する。<br>
   * 日付別のできたことの内容、学習時間、メモをリスト形式に変換し、データオブジェクトに追加する。<br>
   * リストサイズがデフォルトサイズに満たない場合は0または空欄を追加する。<br>
   * 
   * @param doneList  できたことリスト
   * @return  変換後のデータオブジェクト
   */
  public DailyDoneDto fromDailyDone(List<DailyDone> doneList){
    DailyDoneDto dto = new DailyDoneDto();

    dto.idList = new ArrayList<>();
    dto.contentList = new ArrayList<>();
    dto.hoursList = new ArrayList<>();
    dto.memoList = new ArrayList<>();

    if(!doneList.isEmpty()){
      dto.date = doneList.getFirst().getDate();
      shapeMultiDataToList(doneList, dto);
    }

    dto.registeredAt = doneList.stream()
        .map(DailyDone :: getRegisteredAt)
        .min(Comparator.naturalOrder())
        .map(DateTimeFormatUtil :: formatDateTime)
        .get();

    dto.updatedAt = doneList.stream()
        .map(DailyDone :: getUpdatedAt)
        .filter(Objects :: nonNull)
        .max(Comparator.naturalOrder())
        .map(DateTimeFormatUtil :: formatDateTime)
        .orElse(null);

    adjustListSize(dto);
    return dto;
  }

  /**
   * できたことのID、内容、学習時間、メモをリスト形式でまとめる。
   *
   * @param doneList  できたことリスト
   * @param dto 「できたこと」のデータオブジェクト
   */
  private void shapeMultiDataToList(List<DailyDone> doneList, DailyDoneDto dto) {
    for(DailyDone done : doneList){
      dto.idList.add(done.getId());
      dto.contentList.add(done.getContent());
      dto.hoursList.add(String.valueOf(done.getHours()));
      dto.memoList.add(done.getMemo());
    }
  }

  /**
   * ID、内容、学習時間、メモのリストがデフォルトサイズに満たない場合は0または空欄を追加する。
   *
   * @param dto 「できたこと」のデータオブジェクト
   */
  private void adjustListSize(DailyDoneDto dto) {
    while(dto.contentList.size() < DEFAULT_SIZE){
      dto.idList.add(0);
      dto.contentList.add("");
      dto.hoursList.add("");
      dto.memoList.add("");
    }
  }
}
