package com.awesomecollect.exception;

import lombok.Getter;
import com.awesomecollect.common.constant.MessageKeys;

/**
 * 現在のパスワードが間違っている場合に投げられる例外クラス。
 */
@Getter
public class IncorrectPasswordException extends RuntimeException {

  private final String fieldName = "currentPassword";
  private final String messageKey = MessageKeys.CURRENT_PASSWORD_INCORRECT;

  public IncorrectPasswordException() {
    super("Incorrect current password");
  }
}
