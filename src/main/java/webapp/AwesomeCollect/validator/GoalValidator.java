package webapp.AwesomeCollect.validator;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.dto.action.request.GoalRequestDto;

/**
 * 目標のカスタムバリデータクラス。<br>
 * DTOアノテーションで制御できないバリデーションを確認する。
 */
@Component
public class GoalValidator implements Validator {

  private final MessageUtil messageUtil;

  private static final String ACHIEVED = "achieved";

  public GoalValidator(MessageUtil messageUtil){
    this.messageUtil = messageUtil;
  }

  @Override
  public boolean supports(@NotNull Class<?> clazz) {
    return GoalRequestDto.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NotNull Object target, @NotNull Errors errors) {
    GoalRequestDto dto = (GoalRequestDto) target;
    validateStatus(dto, errors);
  }

  // 新規登録時に「達成！」を選択している場合はエラーに追加する。
  private void validateStatus(GoalRequestDto dto, Errors errors){
    if(dto.getId() == 0 && dto.getStatus().equals(ACHIEVED)){
      errors.rejectValue(
          "status", "achievedGoal",
          messageUtil.getMessage(MessageKeys.GOAL_ALREADY_ACHIEVED));
    }
  }
}
