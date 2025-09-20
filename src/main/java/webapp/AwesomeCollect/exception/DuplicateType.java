package webapp.AwesomeCollect.exception;

import lombok.Getter;
import webapp.AwesomeCollect.common.constant.MessageKeys;

/**
 * 重複の種類を扱うenum。
 */
@Getter
public enum DuplicateType {
  USER_ID("loginId",
      "basicInfoDto.loginId",
      MessageKeys.DUPLICATE_LOGIN_ID),
  EMAIL("email",
      "basicInfoDto.email",
      MessageKeys.DUPLICATE_EMAIL);


  // 分割DTO用
  private final String fieldName;
  // 統合DTO用
  private final String compositeFiledName;
  private final String messageKey;

  DuplicateType(String fieldName, String compositeFiledName, String messageKey) {
    this.fieldName = fieldName;
    this.compositeFiledName = compositeFiledName;
    this.messageKey = messageKey;
  }
}
