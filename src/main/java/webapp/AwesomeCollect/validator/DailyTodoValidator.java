package webapp.AwesomeCollect.validator;

import java.util.HashSet;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.dto.action.request.TodoRequestDto;

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
    validateContentList(dto, errors);
    validateContent(dto, errors);
  }

  /**
   * 内容がすべて空欄の場合はエラーに追加する。
   *
   * @param dto やること入力用データオブジェクト
   * @param errors  エラー
   */
  private void validateContentList(TodoRequestDto dto, Errors errors){
    if(dto.getContentList() == null ||
        dto.getContentList().stream()
            .allMatch(content -> content == null || content.isBlank())){

      errors.rejectValue(
          "contentList", "blankContent",
          messageUtil.getMessage(MessageKeys.CONTENT_BLANK));
    }
  }

  /**
   * 同じ内容が含まれている場合はエラーに追加する。
   *
   * @param dto やること入力用データオブジェクト
   * @param errors  エラー
   */
  private void validateContent(TodoRequestDto dto, Errors errors){
    List<String> contentList = dto.getContentList();
    if(contentList == null ||
        contentList.stream()
            .allMatch(content -> content == null || content.isBlank())){

      return;
    }

    contentList.replaceAll(String::strip);
    HashSet<String> uniqueElements = new HashSet<>(contentList);
    if(contentList.size() > uniqueElements.size()){
      errors.rejectValue(
          "contentList", "duplicateContent",
          messageUtil.getMessage(MessageKeys.CONTENT_DUPLICATE));
    }
  }
}
