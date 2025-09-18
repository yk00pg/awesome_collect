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
import webapp.AwesomeCollect.dto.action.DoneRequestDto;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.action.DailyDoneService;
import webapp.AwesomeCollect.service.action.DailyTodoService;
import webapp.AwesomeCollect.validation.DailyDoneValidator;

/**
 * できたことのコントローラークラス。
 */
@Controller
public class DailyDoneController {

  private final DailyDoneService dailyDoneService;
  private final DailyTodoService dailyTodoService;
  private final TagService tagService;
  private final DailyDoneValidator dailyDoneValidator;
  private final MessageUtil messageUtil;

  public DailyDoneController(
      DailyDoneService dailyDoneService, DailyTodoService dailyTodoService,
      TagService tagService, DailyDoneValidator dailyDoneValidator,
      MessageUtil messageUtil){

    this.dailyDoneService = dailyDoneService;
    this.dailyTodoService = dailyTodoService;
    this.tagService = tagService;
    this.dailyDoneValidator = dailyDoneValidator;
    this.messageUtil = messageUtil;
  }

  // できたことリストの閲覧ページを表示
  @GetMapping(ViewNames.DAILY_DONE_VIEW_PAGE)
  public String showDailyDone(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    model.addAttribute(
        AttributeNames.DONE_RESPONSE_DTO,
        dailyDoneService.prepareResponseDto(customUserDetails.getId(), date));

    return ViewNames.DONE_PAGE;
  }

  // できたことリストの閲覧ページにリダイレクト
  @GetMapping(ViewNames.DONE_PAGE)
  public String redirectByDate(@RequestParam LocalDate date) {
    return RedirectUtil.redirectView(ViewNames.DONE_PAGE, date);
  }

  // できたことリストの編集ページを表示
  @GetMapping(ViewNames.DAILY_DONE_EDIT_PAGE)
  public String showDailyDoneForm(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    int userId = customUserDetails.getId();

    model.addAttribute(
        AttributeNames.TODO_RESPONSE_DTO,
        dailyTodoService.prepareResponseDto(userId, date));
    model.addAttribute(
        AttributeNames.DONE_REQUEST_DTO,
        dailyDoneService.prepareRequestDto(userId, date));
    model.addAttribute(
        AttributeNames.TAG_NAME_LIST,
        tagService.prepareTagListByUserId(userId));

    return ViewNames.DONE_EDIT_PAGE;
  }

  // DTOのアノテーションで制御できないバリデーションを確認
  @InitBinder(AttributeNames.DONE_REQUEST_DTO)
  public void initBinder(WebDataBinder dataBinder){
    dataBinder.addValidators(dailyDoneValidator);
  }

  /**
   * 入力されたデータを確認し、できたことを編集する。<br>
   * バインディングエラーが発生した場合は参考用やることリストとタグリストを詰め直して編集ページに戻り、
   * そうでない場合はDBの登録・更新・削除処理を行い、閲覧ページに遷移してサクセスメッセージを表示する。
   *
   * @param date  日付
   * @param dto できたことのデータオブジェクト
   * @param result  バインディングの結果
   * @param model データをViewに渡すオブジェクト
   * @param customUserDetails カスタムユーザー情報
   * @param redirectAttributes  リダイレクト後に一度だけ表示するデータをViewに渡すインターフェース
   * @return  できたこと閲覧ページ
   */
  @PostMapping(ViewNames.DAILY_DONE_EDIT_PAGE)
  public String editDailyDone(
      @PathVariable LocalDate date,
      @Valid @ModelAttribute(AttributeNames.DONE_REQUEST_DTO) DoneRequestDto dto,
      BindingResult result, Model model,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {

    int userId = customUserDetails.getId();

    if(result.hasErrors()){
      model.addAttribute(
          AttributeNames.TODO_RESPONSE_DTO,
          dailyTodoService.prepareResponseDto(userId, date));
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.prepareTagListByUserId(userId));

      return ViewNames.DONE_EDIT_PAGE;
    }

    dailyDoneService.saveDailyDone(userId, dto);

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
          messageUtil.getMessage(MessageKeys.DONE_AWESOME));
    }

    return RedirectUtil.redirectView(ViewNames.DONE_PAGE, date);
  }

  // 指定の日付のできたことリストを削除して閲覧ページにリダイレクト
  @DeleteMapping(ViewNames.DAILY_DONE_VIEW_PAGE)
  public String deleteDone(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes){

    dailyDoneService.deleteDailyDoneByDate(customUserDetails.getId(), date);

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.DELETE_SUCCESS));

    return RedirectUtil.redirectView(ViewNames.DONE_PAGE, date);
  }
}
