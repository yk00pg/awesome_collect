package com.awesomecollect.controller.action;

import jakarta.validation.Valid;
import org.jetbrains.annotations.Nullable;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.awesomecollect.common.SaveResult;
import com.awesomecollect.common.constant.AttributeNames;
import com.awesomecollect.common.constant.MappingValues;
import com.awesomecollect.common.constant.MessageKeys;
import com.awesomecollect.common.constant.TemplateNames;
import com.awesomecollect.common.util.MessageUtil;
import com.awesomecollect.common.util.RedirectUtil;
import com.awesomecollect.dto.action.request.GoalRequestDto;
import com.awesomecollect.dto.action.response.GoalResponseDto;
import com.awesomecollect.exception.DuplicateException;
import com.awesomecollect.security.CustomUserDetails;
import com.awesomecollect.service.TagService;
import com.awesomecollect.service.action.GoalService;
import com.awesomecollect.validator.GoalValidator;

/**
 * 目標のコントローラークラス。
 */
@Controller
public class GoalController {

  private final GoalService goalService;
  private final TagService tagService;
  private final GoalValidator goalValidator;
  private final MessageUtil messageUtil;

  public GoalController(
      GoalService goalService, TagService tagService,
      GoalValidator goalValidator, MessageUtil messageUtil) {

    this.goalService = goalService;
    this.tagService = tagService;
    this.goalValidator = goalValidator;
    this.messageUtil = messageUtil;
  }

  // 目標の一覧ページ（目標リスト）を表示する。
  @GetMapping(MappingValues.GOAL)
  public String showGoal(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    model.addAttribute(
        AttributeNames.GOAL_RESPONSE_DTO_LIST,
        goalService.prepareResponseDtoList(customUserDetails.getId()));

    return TemplateNames.GOAL;
  }

  // 目標の詳細ページを表示する。
  @GetMapping(MappingValues.GOAL_DETAIL_BY_ID)
  public String showGoalDetail(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    GoalResponseDto goalResponseDto =
        goalService.prepareResponseDto(id, customUserDetails.getId());

    if (goalResponseDto == null) {
      return RedirectUtil.redirectView(MappingValues.ERROR_NOT_ACCESSIBLE);
    } else {
      model.addAttribute(AttributeNames.GOAL_RESPONSE_DTO, goalResponseDto);
      return TemplateNames.GOAL_DETAIL;
    }
  }

  // 目標の編集ページを表示する。
  @GetMapping(MappingValues.GOAL_EDIT_BY_ID)
  public String showGoalForm(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    int userId = customUserDetails.getId();
    GoalRequestDto goalRequestDto = goalService.prepareRequestDto(id, userId);

    if (goalRequestDto == null) {
      return RedirectUtil.redirectView(MappingValues.ERROR_NOT_ACCESSIBLE);
    } else {
      model.addAttribute(AttributeNames.GOAL_REQUEST_DTO, goalRequestDto);
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.getTagNameListByUserId(userId));

      return TemplateNames.GOAL_EDIT;
    }
  }

  // DTOのアノテーションで制御できないバリデーションを確認する。
  @InitBinder(AttributeNames.GOAL_REQUEST_DTO)
  public void initBinder(WebDataBinder dataBinder) {
    dataBinder.addValidators(goalValidator);
  }

  /**
   * 入力されたデータにバインディングエラーまたは例外が発生した場合はタグリストを詰め直して
   * 編集ページに戻り、エラーメッセージを表示する。そうでない場合はDBにデータを
   * 保存（登録・更新）し、詳細ページに遷移して保存の種類に応じたサクセスメッセージを表示する。
   *
   * @param id                 目標ID
   * @param dto                目標入力用データオブジェクト
   * @param result             バインディングの結果
   * @param model              データをViewに渡すオブジェクト
   * @param customUserDetails  カスタムユーザー情報
   * @param redirectAttributes リダイレクト後に一度だけ表示するデータをViewに渡すインターフェース
   * @return 目標・詳細ページ
   */
  @PostMapping(MappingValues.GOAL_EDIT_BY_ID)
  public String editGoal(
      @PathVariable int id,
      @Valid @ModelAttribute(AttributeNames.GOAL_REQUEST_DTO) GoalRequestDto dto,
      BindingResult result, Model model,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {

    int userId = customUserDetails.getId();

    if (result.hasErrors()) {
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.getTagNameListByUserId(userId));
      return TemplateNames.GOAL_EDIT;
    }

    SaveResult saveResult = trySaveGoal(dto, result, model, userId);
    if (saveResult == null) {
      return TemplateNames.GOAL_EDIT;
    }

    addAttributeBySaveType(id, redirectAttributes, saveResult);

    return RedirectUtil.redirectView(MappingValues.GOAL_DETAIL, saveResult.id());
  }

  // DBへの保存を試みて保存結果を取得する。
  private @Nullable SaveResult trySaveGoal(
      GoalRequestDto dto, BindingResult result, Model model, int userId) {

    SaveResult saveResult;
    try {
      saveResult = goalService.saveGoal(userId, dto);
    } catch (DuplicateException ex) {
      result.rejectValue(
          ex.getType().getFieldName(), "duplicate",
          messageUtil.getMessage(ex.getType().getMessageKey("goal")));

      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.getTagNameListByUserId(userId));

      return null;
    }
    return saveResult;
  }

  // 登録か更新か、更新の場合は進捗状況が更新されたかを判定してサクセスメッセージとポップアップウィンドウを表示する。
  private void addAttributeBySaveType(
      int id, RedirectAttributes redirectAttributes, SaveResult saveResult) {

    boolean isRegistration = id == 0;
    if (isRegistration) {
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.REGISTER_SUCCESS));
      redirectAttributes.addFlashAttribute(
          AttributeNames.ACHIEVEMENT_POPUP,
          messageUtil.getMessage(MessageKeys.GOAL_AWESOME));
    } else {
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.UPDATE_SUCCESS));

      if (saveResult.isUpdatedStatus()) {
        redirectAttributes.addFlashAttribute(
            AttributeNames.ACHIEVEMENT_POPUP,
            messageUtil.getMessage(MessageKeys.ACHIEVED_AWESOME));
      }
    }
  }

  // 指定のIDの目標を削除して一覧ページにリダイレクトする。
  @DeleteMapping(MappingValues.GOAL_DETAIL_BY_ID)
  public String deleteGoal(
      @PathVariable int id, RedirectAttributes redirectAttributes) {

    goalService.deleteGoal(id);

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.DELETE_SUCCESS));

    return RedirectUtil.redirectView(MappingValues.GOAL);
  }
}
