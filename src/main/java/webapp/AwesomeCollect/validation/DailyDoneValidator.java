package webapp.AwesomeCollect.validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.dto.action.DoneRequestDto;

@Component
public class DailyDoneValidator implements Validator {

  private final MessageUtil messageUtil;

  public DailyDoneValidator(MessageUtil messageUtil){
    this.messageUtil = messageUtil;
  }

  @Override
  public boolean supports(@NotNull Class<?> clazz) {
    return DoneRequestDto.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NotNull Object target, @NotNull Errors errors) {
    DoneRequestDto dto = (DoneRequestDto)  target;
    validateDate(dto, errors);
    validateContent(dto, errors);
    validateHours(dto, errors);
    validateTotalHours(dto, errors);
  }

  private void validateDate(DoneRequestDto dto, Errors errors){
    if(dto.getDate().isAfter(LocalDate.now())){
      errors.rejectValue(
          "date", "futureDate",
          messageUtil.getMessage(MessageKeys.DATE_FUTURE));
    }
  }

  private void validateContent(DoneRequestDto dto, Errors errors){
    if(dto.getContentList() == null ||
        dto.getContentList().stream()
            .allMatch(content -> content == null || content.isBlank())){

      errors.rejectValue(
          "contentList", "blankContent",
          messageUtil.getMessage(MessageKeys.CONTENT_BLANK));
    }
  }

  private void validateHours(DoneRequestDto dto, Errors errors){
    for(int i = 0; i < dto.getContentList().size(); i++){
      String content = dto.getContentList().get(i);
      String hours = dto.getHoursList().get(i);

      if(content != null && !content.isBlank()
          && (hours == null || hours.isBlank()
          || new BigDecimal(hours).compareTo(BigDecimal.ZERO) == 0)){

        errors.rejectValue(
            "hoursList[" + i + "]", "hours.blank",
            messageUtil.getMessage(MessageKeys.HOURS_BLANK));
      }
    }
  }

  private void validateTotalHours(DoneRequestDto dto, Errors errors){
    List<String> hoursList = dto.getHoursList();
    if(hoursList == null
        || hoursList.stream()
        .allMatch(hours -> hours == null || hours.isBlank())){
      return;
    }

    boolean hasExceptZero = hoursList.stream()
        .noneMatch(str -> str.equals("0") || str.equals("0.0") || str.equals("0.00"));
    if(!hasExceptZero){
      return;
    }

    BigDecimal totalHours = BigDecimal.ZERO;
    for(String hourStr : hoursList){
      totalHours = totalHours.add(new BigDecimal(hourStr));
    }

    if(totalHours.compareTo(new BigDecimal("24.00")) > 0){
      errors.rejectValue(
          "hoursList", "exceededTotalHours",
          messageUtil.getMessage(MessageKeys.TOTAL_HOURS_EXCEEDED));
    }
  }
}
