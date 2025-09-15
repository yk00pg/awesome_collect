package webapp.AwesomeCollect.dto.action;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;
import webapp.AwesomeCollect.entity.action.ArticleStock;

/**
 * 記事ストックの入力用データオブジェクト。
 */
@Data
public class ArticleRequestDto {

  private static final int TITLE_MAX_SIZE = 100;
  private static final int URL_MAX_SIZE = 2083;
  private static final int MEMO_MAX_SIZE = 500;

  private static final String STILL_NOT = "stillNot";
  private static final String FINISHED = "finished";
  private static final String STILL_NOT_LABEL = "これから読む";
  private static final String FINISHED_LABEL = "もう読んだ！";
  private static final String UNSET_LABEL = "未設定";

  private int id;

  @NotBlank(message = "{title.blank}")
  @Size(max = TITLE_MAX_SIZE, message = "{title.size}")
  private String title;

  @Size(max = URL_MAX_SIZE, message = "{url.size}")
  private String url;

  @Size(max = MEMO_MAX_SIZE, message = "{memo.size}")
  private String memo;

  @NotBlank(message = "{status.blank}")
  private String status;

  private String tags;

  // 進捗状況を日本語ラベルに変換
  public String getStatusLabel(){
    return switch (status){
      case STILL_NOT -> STILL_NOT_LABEL;
      case FINISHED -> FINISHED_LABEL;
      default -> UNSET_LABEL;
    };
  }

  // 登録用のデータを詰めたエンティティに変換
  public ArticleStock toArticleStockForRegistration(int userId){
    ArticleStock articleStock = new ArticleStock();
    articleStock.setUserId(userId);
    articleStock.setTitle(title);
    articleStock.setUrl(url);
    articleStock.setMemo(memo);
    articleStock.setFinished(status.equals(FINISHED));
    articleStock.setRegisteredAt(LocalDateTime.now());
    return articleStock;
  }

  // 更新用のデータを詰めたエンティティに変換
  public ArticleStock toArticleStockForUpdate(int userId){
    ArticleStock articleStock = new ArticleStock();
    articleStock.setId(id);
    articleStock.setUserId(userId);
    articleStock.setTitle(title);
    articleStock.setUrl(url);
    articleStock.setMemo(memo);
    articleStock.setFinished(status.equals(FINISHED));
    articleStock.setUpdatedAt(LocalDateTime.now());
    return articleStock;
  }

  // DBから取得した記事ストックをデータオブジェクトに変換
  public static ArticleRequestDto fromEntity(ArticleStock articleStock){
    ArticleRequestDto dto = new ArticleRequestDto();
    dto.id = articleStock.getId();
    dto.title = articleStock.getTitle();
    dto.url = articleStock.getUrl();
    dto.memo = articleStock.getMemo();
    dto.status = articleStock.isFinished() ? FINISHED : STILL_NOT;
    return dto;
  }
}
