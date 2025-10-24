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
import webapp.AwesomeCollect.common.constant.MappingValues;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.constant.TemplateNames;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.common.util.RedirectUtil;
import webapp.AwesomeCollect.dto.action.request.TodoRequestDto;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.action.DailyTodoService;
import webapp.AwesomeCollect.validator.DailyTodoValidator;

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
      MessageUtil messageUtil) {

    this.dailyTodoService = dailyTodoService;
    this.dailyTodoValidator = dailyTodoValidator;
    this.messageUtil = messageUtil;
  }

  // やること閲覧ページ（やることリスト）を表示する。
  @GetMapping(MappingValues.DAILY_TODO)
  public String showDailyTodo(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    model.addAttribute(
        AttributeNames.TODO_RESPONSE_DTO,
        dailyTodoService.prepareResponseDto(customUserDetails.getId(), date));

    return TemplateNames.TODO;
  }

  // やること閲覧ページにリダイレクトする。
  @GetMapping(MappingValues.TODO)
  public String redirectByDate(@RequestParam LocalDate date) {
    return RedirectUtil.redirectView(TemplateNames.TODO, date);
  }

  // やること編集ページを表示する。
  @GetMapping(MappingValues.DAILY_TODO_EDIT)
  public String showDailyTodoForm(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    model.addAttribute(
        AttributeNames.TODO_REQUEST_DTO,
        dailyTodoService.prepareRequestDto(customUserDetails.getId(), date));

    return TemplateNames.TODO_EDIT;
  }

  // DTOアノテーションで制御できないバリデーションを確認する。
  @InitBinder(AttributeNames.TODO_REQUEST_DTO)
  public void initBinder(WebDataBinder dataBinder) {
    dataBinder.addValidators(dailyTodoValidator);
  }

  /**
   * 入力されたデータにバンディングエラーが発生した場合は編集ページに戻って
   * エラーメッセージを表示し、そうでない場合はDBにデータを保存（登録・更新・削除）し、
   * 閲覧ページに遷移して保存の種類に応じたサクセスメッセージを表示する。
   *
   * @param date               日付
   * @param dto                やること入力用データオブジェクト
   * @param result             バインディングの結果
   * @param customUserDetails  カスタムユーザー情報
   * @param redirectAttributes リダイレクト後に一度だけ表示されるデータをViewに渡すインターフェース
   * @return やること閲覧ページ
   */
  @PostMapping(MappingValues.DAILY_TODO_EDIT)
  public String editDailyTodo(
      @PathVariable LocalDate date,
      @Valid @ModelAttribute(AttributeNames.TODO_REQUEST_DTO) TodoRequestDto dto,
      BindingResult result,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
      return TemplateNames.TODO_EDIT;
    }

    dailyTodoService.saveDailyTodo(customUserDetails.getId(), dto);
    addAttributeBySaveType(dto, redirectAttributes);

    return RedirectUtil.redirectView(TemplateNames.TODO, date);
  }

  // 新規登録か更新（削除含む）かを判定してサクセスメッセージとポップアップウィンドウを表示する。
  private void addAttributeBySaveType(
      TodoRequestDto dto, RedirectAttributes redirectAttributes) {

    boolean isFirstRegistration = dto.getIdList().getFirst() == 0;
    if (isFirstRegistration) {
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.REGISTER_SUCCESS));
      redirectAttributes.addFlashAttribute(
          AttributeNames.ACHIEVEMENT_POPUP,
          messageUtil.getMessage(MessageKeys.TODO_AWESOME));
    } else {
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.UPDATE_SUCCESS));
    }
  }

  // 指定の日付のやることをすべて削除して閲覧ページにリダイレクトする。
  @DeleteMapping(MappingValues.DAILY_TODO)
  public String deleteDailyAllTodo(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {

    dailyTodoService.deleteDailyAllTodo(customUserDetails.getId(), date);

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.DELETE_SUCCESS));

    return RedirectUtil.redirectView(TemplateNames.TODO, date);
  }
}
