package webapp.AwesomeCollect.dto.action.response;

import java.util.List;
import lombok.Data;
import webapp.AwesomeCollect.common.util.DateTimeFormatUtil;
import webapp.AwesomeCollect.common.util.MarkdownConverter;
import webapp.AwesomeCollect.entity.action.Memo;

/**
 * メモの表示用データオブジェクト。
 */
@Data
public class MemoResponseDto {

  private int id;
  private String title;
  private String content;
  private List<String> tagList;

  private String registeredAt;
  private String updatedAt;

  // 内容をHTMLに変換する（Thymeleaf用）。
  public String getContentAsHtml(){
    return MarkdownConverter.toSafeHtml(content);
  }

  /**
   * DBから取得したメモを一覧ページ用データオブジェクトに変換する。
   *
   * @param memo  メモ
   * @return  一覧ページ用データオブジェクト
   */
  public static MemoResponseDto fromEntityForList(Memo memo){
    MemoResponseDto dto = new MemoResponseDto();
    dto.id = memo.getId();
    dto.title = memo.getTitle();
    return dto;
  }

  /**
   * DBから取得したメモを詳細ページ用データオブジェクトに変換する。
   *
   * @param memo  メモ
   * @return  詳細ページ用データオブジェクト
   */
  public static MemoResponseDto fromEntityForDetail(Memo memo){
    MemoResponseDto dto = new MemoResponseDto();
    dto.id = memo.getId();
    dto.title = memo.getTitle();
    dto.content = memo.getContent();
    dto.registeredAt = DateTimeFormatUtil.formatDateTime(memo.getRegisteredAt());
    dto.updatedAt = DateTimeFormatUtil.formatDateTime(memo.getUpdatedAt());
    return dto;
  }
}
