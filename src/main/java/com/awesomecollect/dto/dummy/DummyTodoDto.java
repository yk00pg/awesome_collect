package com.awesomecollect.dto.dummy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import org.apache.commons.csv.CSVRecord;
import com.awesomecollect.common.constant.CsvHeader;
import com.awesomecollect.entity.action.DailyTodo;

/**
 * ダミーデータ用やることデータオブジェクト。
 */
@Data
public class DummyTodoDto {

  private LocalDate date;
  private String content;

  /**
   * ダミーデータ用データオブジェクトをエンティティに変換する。
   *
   * @param guestUserId ゲストユーザーID
   * @param date  日付
   * @return  やること
   */
  public DailyTodo toEntity(int guestUserId, LocalDate date){
    DailyTodo dailyTodo = new DailyTodo();
    dailyTodo.setUserId(guestUserId);
    dailyTodo.setDate(date);
    dailyTodo.setContent(content);
    dailyTodo.setRegisteredAt(LocalDateTime.now());
    return dailyTodo;
  }


  /**
   * CSVファイルから読み込んだレコードをダミーデータ用データオブジェクトに変換する。
   *
   * @param record  CSVファイルから読み込んだレコード
   * @return  ダミーデータ用データオブジェクト
   */
  public static DummyTodoDto fromCsvRecord(CSVRecord record){
    DummyTodoDto dto = new DummyTodoDto();
    dto.date = LocalDate.parse(record.get(CsvHeader.DATE));
    dto.content = record.get(CsvHeader.CONTENT);
    return dto;
  }
}
