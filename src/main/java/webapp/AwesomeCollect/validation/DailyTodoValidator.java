package webapp.AwesomeCollect.validation;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.dto.action.TodoRequestDto;

/**
 * やることのカスタムバリデータクラス。<br>
 * DTOアノテーションで制御できないバリデーションを確認する。
 */
@Component
public class DailyTodoValidator implements Validator {

  private final MessageUtil messageUtil;

  public DailyTodoValidator(MessageUtil messageUtil){
    this.messageUtil = messageUtil;
  }

  @Override
  public boolean supports(@NotNull Class<?> clazz) {
    return TodoRequestDto.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NotNull Object target, @NotNull Errors errors) {
    TodoRequestDto dto = (TodoRequestDto) target;
    validateContent(dto, errors);
  }

  /**
   * 内容がすべて空欄の場合はエラーに追加する。
   *
   * @param dto やることの入力用データオブジェクト
   * @param errors  エラー
   */
  private void validateContent(TodoRequestDto dto, Errors errors){
    if(dto.getContentList() == null ||
        dto.getContentList().stream()
            .allMatch(content -> content == null || content.isBlank())){

      errors.rejectValue(
          "contentList", "blankContent",
          messageUtil.getMessage(MessageKeys.CONTENT_BLANK));
    }
  }
}
