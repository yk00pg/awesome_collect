package com.awesomecollect.dto.dummy;

import com.awesomecollect.common.constant.CsvHeader;
import com.awesomecollect.entity.action.Memo;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.apache.commons.csv.CSVRecord;

/**
 * ダミーデータ用メモデータオブジェクト
 */
@Data
public class DummyMemoDto {

  private String title;
  private String content;
  private List<String> tagList;

  /**
   * ダミーデータ用データオブジェクトをエンティティに変換する。
   *
   * @param guestUserId ゲストユーザーID
   * @param now 現在の日時
   * @return  メモ
   */
  public Memo toEntity(int guestUserId, LocalDateTime now){
    Memo memo = new Memo();
    memo.setUserId(guestUserId);
    memo.setTitle(title);
    memo.setContent(content);
    memo.setRegisteredAt(now);
    return memo;
  }

  /**
   * CSVファイルから読み込んだレコードをダミーデータ用データオブジェクトに変換する。
   *
   * @param record  CSVファイルから読み込んだレコード
   * @return  ダミーデータ用データオブジェクト
   */
  public static DummyMemoDto fromCsvRecord(CSVRecord record){
    DummyMemoDto dto = new DummyMemoDto();
    dto.title = record.get(CsvHeader.TITLE);
    dto.content = record.get(CsvHeader.CONTENT);

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
