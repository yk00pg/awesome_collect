package webapp.AwesomeCollect.dto.action;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import webapp.AwesomeCollect.common.util.DateTimeFormatUtil;
import webapp.AwesomeCollect.common.util.MarkdownConverter;
import webapp.AwesomeCollect.entity.action.Memo;

@Getter
@Setter
public class MemoDto extends BaseActionDto<Memo, MemoDto> {

  private int id;

  @Size(max = 100, message = "タイトルは100字以内で入力してください")
  private String title;

  private String content;

  private String tags;

  private String registeredAt;
  private String updatedAt;

  /**
   * 内容を安全なHTMLに変換して取得する。
   *
   * @return  変換後の内容
   */
  public String getContentAsHtml(){
    return MarkdownConverter.toSafeHtml(this.content);
  }

  /**
   * 内容が入力されているか判定する。
   *
   * @return  内容が入力されているかどうか
   */
  public boolean isTitleEmpty(){
    return title == null || title.trim().isEmpty();
  }

  /**
   * データオブジェクトをエンティティに変換する。
   *
   * @param userId  ユーザーID
   * @return  変換後のエンティティ
   */
  public Memo toMemo(int userId){
    Memo memo = new Memo();
    memo.setUserId(userId);
    memo.setTitle(this.title);
    memo.setContent(this.content);
    memo.setRegisteredAt(LocalDateTime.now());
    return memo;
  }

  /**
   * データオブジェクトをID付きでエンティティに変換する。
   *
   * @param userId　ユーザーID
   * @return  変換後のエンティティ（ID付き）
   */
  public Memo toMemoWithId(int userId){
    Memo memo = new Memo();
    memo.setId(this.id);
    memo.setUserId(userId);
    memo.setTitle(this.title);
    memo.setContent(this.content);
    memo.setUpdatedAt(LocalDateTime.now());
    return memo;
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
  public List<MemoDto> fromEntityList(List<Memo> memoList) {
    List<MemoDto> dtoList = new ArrayList<>();
    for(Memo memo : memoList){
      MemoDto dto = new MemoDto();
      dto.id = memo.getId();
      dto.title = memo.getTitle();
      dto.content = memo.getContent();
      dtoList.add(dto);
    }
    return dtoList;
  }

  @Override
  public MemoDto fromEntity(Memo memo) {
    MemoDto dto = new MemoDto();
    dto.id = memo.getId();
    dto.title = memo.getTitle();
    dto.content = memo.getContent();
    dto.registeredAt = DateTimeFormatUtil.formatDateTime(memo.getRegisteredAt());
    dto.updatedAt = DateTimeFormatUtil.formatDateTime(memo.getUpdatedAt());

    return dto;
  }
}
