package com.awesomecollect.exception;

import lombok.Getter;
import com.awesomecollect.common.constant.MessageKeys;

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
      MessageKeys.DUPLICATE_EMAIL),
  TITLE("title",
      "",
      MessageKeys.DUPLICATE_TITLE),
  URL("url",
      "",
      MessageKeys.DUPLICATE_URL);

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

  /**
   * （タイトル用）アクションの種類に応じたメッセージキーを取得する。
   *
   * @param action  アクション
   * @return  メッセージキー
   */
  public String getMessageKey(String action){
    if(this == TITLE){
      return switch (action) {
        case "goal" -> MessageKeys.DUPLICATE_GOAL_TITLE;
        case "memo" -> MessageKeys.DUPLICATE_MEMO_TITLE;
        case "article" -> MessageKeys.DUPLICATE_ARTICLE_TITLE;
        default -> this.messageKey;
      };
    }
    return this.messageKey;
  }
}
