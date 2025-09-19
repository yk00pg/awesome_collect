package webapp.AwesomeCollect.dto.action;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;
import webapp.AwesomeCollect.entity.action.Memo;

/**
 * メモの入力用データオブジェクト。
 */
@Data
public class MemoRequestDto {

  private static final int MEMO_MAX_SIZE = 100;

  private int id;

  @NotBlank(message = "{title.blank}")
  @Size(max = MEMO_MAX_SIZE, message = "{title.size}")
  private String title;

  @NotBlank(message = "{content.blank}")
  private String content;

  private String tags;

  // 登録用のデータを詰めたエンティティに変換
  public Memo toMemoForRegistration(int userId){
    Memo memo = new Memo();
    memo.setUserId(userId);
    memo.setTitle(title);
    memo.setContent(content);
    memo.setRegisteredAt(LocalDateTime.now());
    return memo;
  }

  // 更新用のデータを詰めたエンティティに変換
  public Memo toMemoForUpdate(int userId){
    Memo memo = new Memo();
    memo.setId(id);
    memo.setUserId(userId);
    memo.setTitle(title);
    memo.setContent(content);
    memo.setUpdatedAt(LocalDateTime.now());
    return memo;
  }

  // DBから取得したメモをデータオブジェクトに変換
  public static MemoRequestDto fromEntity(Memo memo){
    MemoRequestDto dto = new MemoRequestDto();
    dto.id = memo.getId();
    dto.title = memo.getTitle();
    dto.content = memo.getContent();
    return dto;
  }
}


