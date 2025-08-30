package webapp.AwesomeCollect.exception;

import lombok.Getter;

/**
 * 現在のパスワードが間違っている場合に投げられる例外クラス。
 */
@Getter
public class IncorrectPasswordException extends RuntimeException {

  private final String fieldName = "currentPassword";
  private final String messageKey = "currentPassword.incorrect";

  public IncorrectPasswordException() {
    super("Incorrect current password");
  }

}
