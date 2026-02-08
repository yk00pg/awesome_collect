package com.awesomecollect.dto.dummy;

import com.awesomecollect.common.constant.CsvHeader;
import com.awesomecollect.entity.action.ArticleStock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.apache.commons.csv.CSVRecord;

/**
 * ダミーデータ用記事ストックデータオブジェクト。
 */
@Data
public class DummyArticleStockDto {

  private static final String FINISHED = "finished";

  private String title;
  private String url;
  private String memo;
  private String status;
  private List<String> tagList;

  /**
   * ダミーデータ用データオブジェクトをエンティティに変換する。
   *
   * @param guestUserId ゲストユーザーID
   * @param now 現在の日時
   * @return  記事ストック
   */
  public ArticleStock toEntity(int guestUserId, LocalDateTime now){
    ArticleStock articleStock = new ArticleStock();
    articleStock.setUserId(guestUserId);
    articleStock.setTitle(title);
    articleStock.setUrl(url);
    articleStock.setMemo(memo);
    articleStock.setFinished(status.equals(FINISHED));
    articleStock.setRegisteredAt(now);
    return articleStock;
  }

  /**
   * CSVファイルから読み込んだレコードをダミーデータ用データオブジェクトに変換する。
   *
   * @param record  CSVファイルから読み込んだレコード
   * @return  ダミーデータ用データオブジェクト
   */
  public static DummyArticleStockDto fromCsvRecord(CSVRecord record){
    DummyArticleStockDto dto = new DummyArticleStockDto();
    dto.title = record.get(CsvHeader.TITLE);
    dto.url = record.get(CsvHeader.URL);
    dto.memo = record.get(CsvHeader.MEMO);
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
