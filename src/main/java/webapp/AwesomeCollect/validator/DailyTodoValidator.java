package webapp.AwesomeCollect.validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

  public DailyTodoValidator(MessageUtil messageUtil) {
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
  }

  // すべての内容が空欄の場合はエラーに追加する。
  private void validateContentList(TodoRequestDto dto, Errors errors) {
    if (dto.getContentList() == null ||
        dto.getContentList().stream()
            .allMatch(content -> content == null || content.isBlank())) {

      errors.rejectValue(
          "contentList", "blankContent",
          messageUtil.getMessage(MessageKeys.CONTENT_BLANK));
    } else {
      validateContent(dto, errors);
    }
  }

  // 内容が重複している場合はエラーに追加する。
  private void validateContent(TodoRequestDto dto, Errors errors) {
    List<String> contentList = dto.getContentList();
    List<String> nonNullContentList =
        new ArrayList<>(contentList.stream()
            .filter(Objects :: nonNull)
            .toList());

    nonNullContentList.replaceAll(String :: strip);
    HashSet<String> uniqueElements = new HashSet<>(nonNullContentList);
    if (nonNullContentList.size() > uniqueElements.size()) {
      errors.rejectValue(
          "contentList", "duplicateContent",
          messageUtil.getMessage(MessageKeys.CONTENT_DUPLICATE));
    }
  }
}
