package webapp.AwesomeCollect.validation;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.dto.user.UserPasswordDto;

@Component
public class MyPageValidator implements Validator {

  private final MessageUtil messageUtil;

  public MyPageValidator(MessageUtil messageUtil){
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

  private void validateCurrentPassword(UserPasswordDto dto, Errors errors){
    if(dto.getCurrentPassword() == null || dto.getCurrentPassword().isBlank()){
      errors.rejectValue(
          "currentPassword", "blankCurrentPassword",
          messageUtil.getMessage(MessageKeys.CURRENT_PASSWORD_BLANK));
    }
  }

  private void validateConfirmPassword(UserPasswordDto dto, Errors errors){
    if(!dto.getPassword().equals(dto.getConfirmPassword())){
      errors.rejectValue(
          "password", "mismatchPassword",
          messageUtil.getMessage(MessageKeys.PASSWORD_MISMATCH));
      errors.rejectValue(
          "confirmPassword", "mismatchPassword",
          messageUtil.getMessage(MessageKeys.PASSWORD_MISMATCH));
    }
  }
}
