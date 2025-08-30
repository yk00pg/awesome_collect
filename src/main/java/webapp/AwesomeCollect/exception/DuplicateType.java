package webapp.AwesomeCollect.exception;

import lombok.Getter;

/**
 * 重複の種類を扱うenum。
 */
@Getter
public enum DuplicateType {
  USER_ID("loginId",
      "basicInfoDto.loginId",
      "duplicate.loginId"),
  EMAIL("email",
      "basicInfoDto.email",
      "duplicate.email");


  // 分割DTO用のフィールド名
  private final String fieldName;
  // 統合DTO用のフィールド名
  private final String compositeFiledName;
  private final String messageKey;

  DuplicateType(String fieldName, String compositeFiledName, String messageKey) {
    this.fieldName = fieldName;
    this.compositeFiledName = compositeFiledName;
    this.messageKey = messageKey;
  }
}
