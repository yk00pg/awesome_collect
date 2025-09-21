package webapp.AwesomeCollect.validator;

import java.time.LocalDate;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.dto.action.request.DoneRequestDto;

/**
 * できたことのカスタムバリデータクラス。<br>
 * DTOアノテーションで制御できないバリデーションを確認する。
 */
@Component
public class DailyDoneValidator implements Validator {

  private final MessageUtil messageUtil;

  private static final int MINUTES = 60;
  private static final int HOURS = 24;

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
    validateLearningTime(dto, errors);
    validateTotalLearningTime(dto, errors);
  }

  // 未来の日付の場合はエラーに追加する。
  private void validateDate(DoneRequestDto dto, Errors errors){
    if(dto.getDate().isAfter(LocalDate.now())){
      errors.rejectValue(
          "date", "futureDate",
          messageUtil.getMessage(MessageKeys.DATE_FUTURE));
    }
  }

  // すべての内容が空欄の場合はエラーに追加する。
  private void validateContent(DoneRequestDto dto, Errors errors){
    if(dto.getContentList() == null ||
        dto.getContentList().stream()
            .allMatch(content -> content == null || content.isBlank())){

      errors.rejectValue(
          "contentList", "blankContent",
          messageUtil.getMessage(MessageKeys.CONTENT_BLANK));
    }
  }

  // 内容が入力されていて、学習時間が入力されていない場合はエラーに追加する。
  private void validateLearningTime(DoneRequestDto dto, Errors errors){
    for(int i = 0; i < dto.getContentList().size(); i++){
      String content = dto.getContentList().get(i);

      if(content != null && !content.isBlank()
          && dto.getHoursList().get(i) == 0 && dto.getMinutesList().get(i) == 0){
        // エラーメッセージを重複して表示しないように、minutesListフィールドには追加しない。
        errors.rejectValue(
            "hoursList[" + i + "]", "blankLearningTime",
            messageUtil.getMessage(MessageKeys.LEARNING_TIME_BLANK));
      }
    }
  }

  // 1日の学習時間尾合計が24時間を超える場合はエラーに追加する。
  private void validateTotalLearningTime(DoneRequestDto dto, Errors errors){
    int totalHours = dto.getHoursList().stream()
        .mapToInt(Integer::intValue)
        .sum();
    int totalMinutes = dto.getMinutesList().stream()
        .mapToInt(Integer::intValue)
        .sum();

    if((totalHours * MINUTES) + totalMinutes > HOURS * MINUTES){
      // エラーメッセージを重複して表示しないように、minutesListフィールドには追加しない。
      errors.rejectValue(
          "hoursList", "exceededTotalLearningTime",
          messageUtil.getMessage(MessageKeys.TOTAL_LEARNING_TIME_EXCEEDED));
    }
  }
}
