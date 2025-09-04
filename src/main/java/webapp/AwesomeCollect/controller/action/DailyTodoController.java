package webapp.AwesomeCollect.controller.action;

import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.constant.ViewNames;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.common.util.RedirectUtil;
import webapp.AwesomeCollect.dto.action.TodoRequestDto;
import webapp.AwesomeCollect.dto.action.TodoResponseDto;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.action.DailyTodoService;

@Controller
public class DailyTodoController {

  private final DailyTodoService dailyTodoService;
  private final MessageUtil messageUtil;

  public DailyTodoController(
      DailyTodoService dailyTodoService, MessageUtil messageUtil){

    this.dailyTodoService = dailyTodoService;
    this.messageUtil = messageUtil;
  }

  // やることリストの閲覧ページを表示
  @GetMapping(ViewNames.DAILY_TODO_VIEW_PAGE)
  public String showDailyTodo(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    TodoResponseDto currentDto =
        dailyTodoService.prepareResponseDto(customUserDetails.getId(), date);
    model.addAttribute(AttributeNames.TODO_RESPONSE_DTO, currentDto);

    return ViewNames.TODO_PAGE;
  }

  // やることリストの閲覧ページにリダイレクト
  @GetMapping(ViewNames.TODO_PAGE)
  public String redirectByDate(@RequestParam LocalDate date) {
    return RedirectUtil.redirectView(ViewNames.TODO_PAGE, date);
  }

  // やることリストの編集ページを表示
  @GetMapping(ViewNames.DAILY_TODO_EDIT_PAGE)
  public String showDailyTodoForm(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    TodoRequestDto currentDto =
        dailyTodoService.prepareRequestDto(customUserDetails.getId(), date);
    model.addAttribute(AttributeNames.TODO_REQUEST_DTO, currentDto);
    return ViewNames.TODO_EDIT_PAGE;
  }

  /**
   * 入力されたデータを確認し、やることを編集する。<br>
   * バンディングエラーが発生した場合はエラーメッセージを表示し、
   * そうでない場合はDBの登録・更新・削除処理を行い、閲覧ページに遷移してサクセスメッセージを表示する。
   *
   * @param date  日付
   * @param dto やることのデータオブジェクト
   * @param result  バインディングの結果
   * @param customUserDetails カスタムユーザー情報
   * @param redirectAttributes  リダイレクト後に一度だけ表示されるデータをViewに渡すインターフェース
   * @return  やること閲覧ページ
   */
  @PostMapping(ViewNames.DAILY_TODO_EDIT_PAGE)
  public String editDailyTodo(
      @PathVariable LocalDate date,
      @Valid @ModelAttribute(AttributeNames.TODO_REQUEST_DTO) TodoRequestDto dto,
      BindingResult result,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes){

    if(result.hasErrors()){
      return ViewNames.TODO_EDIT_PAGE;
    }

    // 内容がすべて空欄の場合はエラーに追加
    if (dto.isContentListEmpty()) {
      result.rejectValue(
          "contentList", "blankContent",
          messageUtil.getMessage(MessageKeys.CONTENT_BLANK));

      return ViewNames.TODO_EDIT_PAGE;
    }

    dailyTodoService.saveDailyTodo(customUserDetails.getId(), dto);

    boolean isUpdatedRecord = dto.getIdList().getFirst() != 0;

    // 新規登録か更新（削除含む）かを判定してサクセスメッセージを表示
    if(isUpdatedRecord){
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.UPDATE_SUCCESS));
    }else{
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.REGISTER_SUCCESS));
      redirectAttributes.addFlashAttribute(
          AttributeNames.ACHIEVEMENT_POPUP,
          messageUtil.getMessage(MessageKeys.TODO_AWESOME));
    }

    return RedirectUtil.redirectView(ViewNames.TODO_PAGE, date);
  }


  @DeleteMapping(ViewNames.DAILY_TODO_VIEW_PAGE)
  public String deleteTodo(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes){

    dailyTodoService.deleteDailyAllTodo(customUserDetails.getId(), date);

    redirectAttributes.addFlashAttribute(AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.DELETE_SUCCESS));

    return RedirectUtil.redirectView(ViewNames.TODO_PAGE, date);
  }
}
