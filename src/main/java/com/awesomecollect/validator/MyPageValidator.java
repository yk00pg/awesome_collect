package com.awesomecollect.validator;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.awesomecollect.common.constant.MessageKeys;
import com.awesomecollect.common.util.MessageUtil;
import com.awesomecollect.dto.user.UserPasswordDto;

/**
 * マイページのユーザー情報編集時のカスタムバリデータクラス。
 */
@Component
public class MyPageValidator implements Validator {

  private final MessageUtil messageUtil;

  public MyPageValidator(MessageUtil messageUtil) {
    this.messageUtil = messageUtil;
  }

  @Override
  public boolean supports(@NotNull Class<?> clazz) {
    return UserPasswordDto.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NotNull Object target, @NotNull Errors errors) {
    UserPasswordDto dto = (UserPasswordDto) target;
    validateCurrentPassword(dto, errors);
    validateConfirmPassword(dto, errors);
  }

  // 現在のパスワードが空欄の場合はエラーに追加する。
  private void validateCurrentPassword(UserPasswordDto dto, Errors errors) {
    if (dto.getCurrentPassword().isBlank()) {
      errors.rejectValue(
          "currentPassword", "blankCurrentPassword",
          messageUtil.getMessage(MessageKeys.CURRENT_PASSWORD_BLANK));
    }
  }

  // パスワードと確認用パスワードが不一致の場合はエラーに追加する。
  private void validateConfirmPassword(UserPasswordDto dto, Errors errors) {
    if (!dto.getPassword().equals(dto.getConfirmPassword())) {
      errors.rejectValue(
          "password", "mismatchPassword",
          messageUtil.getMessage(MessageKeys.PASSWORD_MISMATCH));
      errors.rejectValue(
          "confirmPassword", "mismatchPassword",
          messageUtil.getMessage(MessageKeys.PASSWORD_MISMATCH));
    }
  }
}
