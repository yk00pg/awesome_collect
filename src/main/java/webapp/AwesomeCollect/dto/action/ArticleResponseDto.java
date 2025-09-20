package webapp.AwesomeCollect.dto.action;

import java.util.List;
import lombok.Data;
import webapp.AwesomeCollect.common.util.DateTimeFormatUtil;
import webapp.AwesomeCollect.entity.action.ArticleStock;

/**
 * 記事ストック表示用データオブジェクト。
 */
@Data
public class ArticleResponseDto {

  private static final String STILL_NOT = "stillNot";
  private static final String FINISHED = "finished";
  private static final String STILL_NOT_LABEL = "これから読む";
  private static final String FINISHED_LABEL = "もう読んだ！";
  private static final String UNSET_LABEL = "未設定";


  private int id;
  private String title;
  private String url;
  private String status;
  private String memo;
  private List<String> tagList;

  private String registeredAt;
  private String updatedAt;

  // 閲覧状況を日本語ラベルに変換する（Thymeleaf用）。
  public String getStatusLabel(){
    return switch (status){
      case STILL_NOT -> STILL_NOT_LABEL;
      case FINISHED -> FINISHED_LABEL;
      default -> UNSET_LABEL;
    };
  }

  /**
   * DBから取得した記事ストックを一覧ページ用データオブジェクトに変換する。
   *
   * @param articleStock  記事ストック
   * @return  一覧ページ用データオブジェクト
   */
  public static ArticleResponseDto fromEntityForList(
      ArticleStock articleStock){

    ArticleResponseDto dto = new ArticleResponseDto();
    dto.id = articleStock.getId();
    dto.title = articleStock.getTitle();
    dto.status = articleStock.isFinished() ? FINISHED : STILL_NOT;
    return dto;
  }

  /**
   * DBから取得した記事ストックを詳細ページ用データオブジェクトに変換する。
   *
   * @param articleStock  記事ストック
   * @return  詳細ページ用データオブジェクト
   */
  public static ArticleResponseDto fromEntityForDetail(
      ArticleStock articleStock) {

    ArticleResponseDto dto = new ArticleResponseDto();
    dto.id = articleStock.getId();
    dto.title = articleStock.getTitle();
    dto.url = articleStock.getUrl();
    dto.status = articleStock.isFinished() ? FINISHED : STILL_NOT;
    dto.memo = articleStock.getMemo();
    dto.registeredAt = DateTimeFormatUtil.formatDateTime(articleStock.getRegisteredAt());
    dto.updatedAt = DateTimeFormatUtil.formatDateTime(articleStock.getUpdatedAt());
    return dto;
  }
}
