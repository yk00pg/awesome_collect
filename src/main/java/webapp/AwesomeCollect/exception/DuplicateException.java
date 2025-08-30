package webapp.AwesomeCollect.exception;

import lombok.Getter;

/**
 * ログインIDまたはメールアドレスが重複している場合に投げられる例外クラス。
 */
@Getter
public class DuplicateException extends RuntimeException{

  private final DuplicateType type;

  public DuplicateException(DuplicateType type) {
    super(type.getMessageKey());
    this.type = type;
  }
}
