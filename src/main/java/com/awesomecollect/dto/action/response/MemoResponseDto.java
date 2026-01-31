package com.awesomecollect.dto.action.response;

import java.util.List;
import lombok.Data;
import com.awesomecollect.common.util.DateTimeFormatUtil;
import com.awesomecollect.common.util.MarkdownConverter;
import com.awesomecollect.entity.action.Memo;

/**
 * メモ表示用データオブジェクト。
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
   * DBから取得したメモを一覧ページの表示用データオブジェクトに変換する。
   *
   * @param memo  メモ
   * @return  一覧ページの表示用データオブジェクト
   */
  public static MemoResponseDto fromEntityForList(Memo memo){
    MemoResponseDto dto = new MemoResponseDto();
    dto.id = memo.getId();
    dto.title = memo.getTitle();
    return dto;
  }

  /**
   * DBから取得したメモを詳細ページの表示用データオブジェクトに変換する。
   *
   * @param memo  メモ
   * @return  詳細ページの表示用データオブジェクト
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
