package com.awesomecollect.dto.action.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;
import com.awesomecollect.entity.action.Memo;

/**
 * メモ入力用データオブジェクト。
 */
@Data
public class MemoRequestDto {

  private static final int TITLE_MAX_SIZE = 100;

  private int id;

  @NotBlank(message = "{title.blank}")
  @Size(max = TITLE_MAX_SIZE, message = "{title.size}")
  private String title;

  @NotBlank(message = "{content.blank}")
  private String content;

  private String tags;

  /**
   * 入力されたデータを新規登録用のエンティティに変換する。
   *
   * @param userId  ユーザーID
   * @return  新規登録用のエンティティ
   */
  public Memo toMemoForRegistration(int userId){
    Memo memo = new Memo();
    memo.setUserId(userId);
    memo.setTitle(title);
    memo.setContent(content);
    memo.setRegisteredAt(LocalDateTime.now());
    return memo;
  }

  /**
   * 入力されたデータを更新用のエンティティに変換する。
   *
   * @param userId  ユーザーID
   * @return  更新用のエンティティ
   */
  public Memo toMemoForUpdate(int userId){
    Memo memo = new Memo();
    memo.setId(id);
    memo.setUserId(userId);
    memo.setTitle(title);
    memo.setContent(content);
    memo.setUpdatedAt(LocalDateTime.now());
    return memo;
  }

  /**
   * DBから取得したメモを入力用データオブジェクトに変換する。
   *
   * @param memo  メモ
   * @return  入力用データオブジェクト
   */
  public static MemoRequestDto fromEntity(Memo memo){
    MemoRequestDto dto = new MemoRequestDto();
    dto.id = memo.getId();
    dto.title = memo.getTitle();
    dto.content = memo.getContent();
    return dto;
  }
}


