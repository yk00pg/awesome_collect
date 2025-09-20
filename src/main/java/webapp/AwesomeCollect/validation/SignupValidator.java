package webapp.AwesomeCollect.validation;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.dto.user.UserInfoDto;
import webapp.AwesomeCollect.dto.user.UserPasswordDto;

/**
 * サインアップ時のカスタムバリデータクラス。
 */
@Component
public class SignupValidator implements Validator {

  private final MessageUtil messageUtil;

  public SignupValidator(MessageUtil messageUtil){
    this.messageUtil = messageUtil;
  }

  @Override
  public boolean supports(@NotNull Class<?> clazz) {
    return UserInfoDto.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NotNull Object target, @NotNull Errors errors) {
    UserInfoDto dto = (UserInfoDto) target;
    validatePassword(dto, errors);
  }

  // パスワードと確認用パスワードが不一致の場合はエラーに追加する。
  private void validatePassword(UserInfoDto dto, Errors errors){
    UserPasswordDto passwordDto = dto.getPasswordDto();
    if(!passwordDto.getPassword().equals(passwordDto.getConfirmPassword())){
      errors.rejectValue(
          "passwordDto.password", "mismatchPassword",
          messageUtil.getMessage(MessageKeys.PASSWORD_MISMATCH));
      errors.rejectValue(
          "passwordDto.confirmPassword", "mismatchPassword",
          messageUtil.getMessage(MessageKeys.PASSWORD_MISMATCH));
    }
  }
}
