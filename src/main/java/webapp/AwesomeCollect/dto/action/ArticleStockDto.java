package webapp.AwesomeCollect.dto.action;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import webapp.AwesomeCollect.common.util.DateTimeFormatUtil;
import webapp.AwesomeCollect.entity.action.ArticleStock;

@Getter
@Setter
public class ArticleStockDto extends BaseActionDto<ArticleStock, ArticleStockDto> {

  private int id;

  @Size(max = 100, message = "タイトルは100字以内で入力してください")
  private String title;

  @Size(max = 2083, message = "URLは2083字以内で入力してください")
  private String url;

  @NotNull(message = "ドロップダウンリストから選択してください")
  private String status;

  private String tags;

  private String registeredAt;
  private String updatedAt;

  public String getStatusLabel(){
    return switch (status){
      case "stillNot" -> "これから読む";
      case "finished" -> "読んだ";
      default -> "未設定";
    };
  }

  public boolean isUrlEmpty(){
    return url == null || url.trim().isEmpty();
  }

  public ArticleStock toArticleStock(int userId){
    ArticleStock articleStock = new ArticleStock();
    articleStock.setUserId(userId);
    articleStock.setTitle(this.title);
    articleStock.setUrl(this.url);
    articleStock.setFinished(this.status.equals("finished"));
    articleStock.setRegisteredAt(LocalDateTime.now());
    return articleStock;
  }

  public ArticleStock toArticleStockWithId(int userId){
    ArticleStock articleStock = new ArticleStock();
    articleStock.setId(this.id);
    articleStock.setUserId(userId);
    articleStock.setTitle(this.title);
    articleStock.setUrl(this.url);
    articleStock.setFinished(this.status.equals("finished"));
    articleStock.setUpdatedAt(LocalDateTime.now());
    return articleStock;
  }

  @Override
  public int getId(){
    return this.id;
  }

  @Override
  public void setId(int id){
    this.id = id;
  }

  @Override
  public void setTags(String tags){
    this.tags = tags;
  }

  @Override
  public List<ArticleStockDto> fromEntityList(List<ArticleStock> articleStockList) {
    List<ArticleStockDto> dtoList = new ArrayList<>();
    for(ArticleStock articleStock : articleStockList){
      ArticleStockDto dto = new ArticleStockDto();
      dto.id = articleStock.getId();
      dto.title = articleStock.getTitle();
      dto.url = articleStock.getUrl();
      dto.status = articleStock.isFinished() ? "finished" : "stillNot";
      dto.registeredAt = DateTimeFormatUtil.formatDateTime(articleStock.getRegisteredAt());
      dto.updatedAt = DateTimeFormatUtil.formatDateTime(articleStock.getUpdatedAt());

      dtoList.add(dto);
    }
    return dtoList;
  }

  @Override
  public ArticleStockDto fromEntity(ArticleStock articleStock) {
    ArticleStockDto dto = new ArticleStockDto();
    dto.id = articleStock.getId();
    dto.title = articleStock.getTitle();
    dto.url = articleStock.getUrl();
    dto.status = articleStock.isFinished() ? "finished" : "stillNot";

    return dto;
  }
}
