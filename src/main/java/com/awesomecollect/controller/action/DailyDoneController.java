package com.awesomecollect.controller.action;

import com.awesomecollect.common.constant.AttributeNames;
import com.awesomecollect.common.constant.MappingValues;
import com.awesomecollect.common.constant.MessageKeys;
import com.awesomecollect.common.constant.TemplateNames;
import com.awesomecollect.common.util.MessageUtil;
import com.awesomecollect.common.util.RedirectUtil;
import com.awesomecollect.controller.web.SessionManager;
import com.awesomecollect.dto.action.request.DoneRequestDto;
import com.awesomecollect.security.CustomUserDetails;
import com.awesomecollect.service.TagService;
import com.awesomecollect.service.action.DailyDoneService;
import com.awesomecollect.service.action.DailyTodoService;
import com.awesomecollect.validator.DailyDoneValidator;
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
  private final SessionManager sessionManager;

  public DailyDoneController(
      DailyDoneService dailyDoneService, DailyTodoService dailyTodoService,
      TagService tagService, DailyDoneValidator dailyDoneValidator,
      MessageUtil messageUtil, SessionManager sessionManager) {

    this.dailyDoneService = dailyDoneService;
    this.dailyTodoService = dailyTodoService;
    this.tagService = tagService;
    this.dailyDoneValidator = dailyDoneValidator;
    this.messageUtil = messageUtil;
    this.sessionManager = sessionManager;
  }

  // できたこと閲覧ページ（できたことリスト）を表示する。
  @GetMapping(MappingValues.DAILY_DONE)
  public String showDailyDone(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    model.addAttribute(
        AttributeNames.DONE_RESPONSE_DTO,
        dailyDoneService.prepareResponseDtoForList(customUserDetails.getId(), date));

    return TemplateNames.DONE;
  }

  // できたこと閲覧ページにリダイレクトする。
  @GetMapping(MappingValues.DONE)
  public String redirectByDate(@RequestParam LocalDate date) {
    return RedirectUtil.redirectView(MappingValues.DONE, date);
  }

  // できたこと編集ページを表示する。
  @GetMapping(MappingValues.DAILY_DONE_EDIT)
  public String showDailyDoneForm(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    int userId = customUserDetails.getId();

    model.addAttribute(
        AttributeNames.TODO_RESPONSE_DTO,
        dailyTodoService.prepareResponseDtoForList(userId, date));
    model.addAttribute(
        AttributeNames.DONE_REQUEST_DTO,
        dailyDoneService.prepareRequestDtoForEdit(userId, date));
    model.addAttribute(
        AttributeNames.TAG_NAME_LIST, tagService.getTagNameListByUserId(userId));

    return TemplateNames.DONE_EDIT;
  }

  // DTOのアノテーションで制御できないバリデーションを確認する。
  @InitBinder(AttributeNames.DONE_REQUEST_DTO)
  public void initBinder(WebDataBinder dataBinder) {
    dataBinder.addValidators(dailyDoneValidator);
  }

  /**
   * 入力されたデータにバインディングエラーが発生した場合は参考用やることリストと
   * タグリストを詰め直して編集ページに戻ってエラーメッセージを表示し、
   * そうでない場合はDBにデータを保存（登録・更新・削除）し、セッション情報を更新する。<br>
   * 閲覧ページに遷移して保存の種類に応じたサクセスメッセージを表示する。
   *
   * @param date               日付
   * @param dto                できたこと入力用データオブジェクト
   * @param result             バインディングの結果
   * @param model              データをViewに渡すオブジェクト
   * @param customUserDetails  カスタムユーザー情報
   * @param redirectAttributes リダイレクト後に一度だけ表示するデータをViewに渡すインターフェース
   * @return できたこと閲覧ページ
   */
  @PostMapping(MappingValues.DAILY_DONE_EDIT)
  public String editDailyDone(
      @PathVariable LocalDate date,
      @Valid @ModelAttribute(AttributeNames.DONE_REQUEST_DTO) DoneRequestDto dto,
      BindingResult result, Model model,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {

    int userId = customUserDetails.getId();

    if (result.hasErrors()) {
      model.addAttribute(
          AttributeNames.TODO_RESPONSE_DTO,
          dailyTodoService.prepareResponseDtoForList(userId, date));
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.getTagNameListByUserId(userId));

      return TemplateNames.DONE_EDIT;
    }

    dailyDoneService.saveDailyDone(userId, dto);
    sessionManager.disableCachedAwesomePoints();
    sessionManager.disableCachedLearningDays();
    sessionManager.disableCachedLearningTime();

    addAttributeBySaveType(dto, redirectAttributes);

    return RedirectUtil.redirectView(MappingValues.DONE, date);
  }

  // 新規登録か更新（削除含む）かを判定してサクセスメッセージとポップアップウィンドウを表示する。
  private void addAttributeBySaveType(
      DoneRequestDto dto, RedirectAttributes redirectAttributes) {

    boolean isFirstRegistration = dto.getIdList().getFirst() == 0;
    if (isFirstRegistration) {
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.REGISTER_SUCCESS));
      redirectAttributes.addFlashAttribute(
          AttributeNames.ACHIEVEMENT_POPUP,
          messageUtil.getMessage(MessageKeys.DONE_AWESOME));
    } else {
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.UPDATE_SUCCESS));
    }
  }

  // 指定の日付のできたことをすべて削除してセッション情報を更新し、閲覧ページにリダイレクトする。
  @DeleteMapping(MappingValues.DAILY_DONE)
  public String deleteDailyAllDone(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {

    dailyDoneService.deleteDailyAllDoneByDate(customUserDetails.getId(), date);
    sessionManager.disableCachedAwesomePoints();
    sessionManager.disableCachedLearningDays();
    sessionManager.disableCachedLearningTime();

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.DELETE_SUCCESS));

    return RedirectUtil.redirectView(MappingValues.DONE, date);
  }
}
