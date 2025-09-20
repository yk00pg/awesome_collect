package webapp.AwesomeCollect.controller.action;

import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
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
import webapp.AwesomeCollect.dto.action.request.TodoRequestDto;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.action.DailyTodoService;
import webapp.AwesomeCollect.validation.DailyTodoValidator;

/**
 * やることのコントローラークラス。
 */
@Controller
public class DailyTodoController {

  private final DailyTodoService dailyTodoService;
  private final DailyTodoValidator dailyTodoValidator;
  private final MessageUtil messageUtil;

  public DailyTodoController(
      DailyTodoService dailyTodoService, DailyTodoValidator dailyTodoValidator,
      MessageUtil messageUtil){

    this.dailyTodoService = dailyTodoService;
    this.dailyTodoValidator = dailyTodoValidator;
    this.messageUtil = messageUtil;
  }

  // やること閲覧ページ（やることリスト）を表示する。
  @GetMapping(ViewNames.DAILY_TODO_VIEW_PAGE)
  public String showDailyTodo(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    model.addAttribute(
        AttributeNames.TODO_RESPONSE_DTO,
        dailyTodoService.prepareResponseDto(customUserDetails.getId(), date));

    return ViewNames.TODO_PAGE;
  }

  // やること閲覧ページにリダイレクトする。
  @GetMapping(ViewNames.TODO_PAGE)
  public String redirectByDate(@RequestParam LocalDate date) {
    return RedirectUtil.redirectView(ViewNames.TODO_PAGE, date);
  }

  // やること編集ページを表示する。
  @GetMapping(ViewNames.DAILY_TODO_EDIT_PAGE)
  public String showDailyTodoForm(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    model.addAttribute(
        AttributeNames.TODO_REQUEST_DTO,
        dailyTodoService.prepareRequestDto(customUserDetails.getId(), date));

    return ViewNames.TODO_EDIT_PAGE;
  }

  // DTOアノテーションで制御できないバリデーションを確認する。
  @InitBinder(AttributeNames.TODO_REQUEST_DTO)
  public void initBinder(WebDataBinder dataBinder){
    dataBinder.addValidators(dailyTodoValidator);
  }

  /**
   * 入力されたデータにバンディングエラーが発生した場合は編集ページに戻り、
   * そうでない場合はDBにデータを保存（登録・更新・削除）し、
   * 閲覧ページに遷移して保存の種類に応じたサクセスメッセージを表示する。
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

    dailyTodoService.saveDailyTodo(customUserDetails.getId(), dto);

    boolean isFirstRegistration = dto.getIdList().getFirst() == 0;
    if(isFirstRegistration){
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.REGISTER_SUCCESS));
      redirectAttributes.addFlashAttribute(
          AttributeNames.ACHIEVEMENT_POPUP,
          messageUtil.getMessage(MessageKeys.TODO_AWESOME));
    }else{
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.UPDATE_SUCCESS));
    }

    return RedirectUtil.redirectView(ViewNames.TODO_PAGE, date);
  }

  // 指定の日付のやることをすべて削除して閲覧ページにリダイレクトする。
  @DeleteMapping(ViewNames.DAILY_TODO_VIEW_PAGE)
  public String deleteDailyAllTodo(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes){

    dailyTodoService.deleteDailyAllTodo(customUserDetails.getId(), date);

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.DELETE_SUCCESS));

    return RedirectUtil.redirectView(ViewNames.TODO_PAGE, date);
  }
}
