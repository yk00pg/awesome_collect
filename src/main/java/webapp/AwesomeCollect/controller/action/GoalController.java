package webapp.AwesomeCollect.controller.action;

import jakarta.validation.Valid;
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
import webapp.AwesomeCollect.common.SaveResult;
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.constant.ViewNames;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.common.util.RedirectUtil;
import webapp.AwesomeCollect.dto.action.request.GoalRequestDto;
import webapp.AwesomeCollect.dto.action.response.GoalResponseDto;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.action.GoalService;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.validation.GoalValidator;

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
      GoalValidator goalValidator, MessageUtil messageUtil){

    this.goalService = goalService;
    this.tagService = tagService;
    this.goalValidator = goalValidator;
    this.messageUtil = messageUtil;
  }

  // 目標の一覧ページ（目標リスト）を表示する。
  @GetMapping(ViewNames.GOAL_PAGE)
  public String showGoal(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    model.addAttribute(
        AttributeNames.GOAL_RESPONSE_DTO_LIST,
        goalService.prepareResponseDtoList(customUserDetails.getId()));

    return ViewNames.GOAL_PAGE;
  }

  // 目標の詳細ページを表示する。
  @GetMapping(ViewNames.GOAL_DETAIL_BY_ID)
  public String showGoalDetail(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    GoalResponseDto goalResponseDto =
        goalService.prepareResponseDto(id, customUserDetails.getId());

    if (goalResponseDto == null) {
      return RedirectUtil.redirectView(ViewNames.ERROR_NOT_ACCESSIBLE);
    } else {
      model.addAttribute(AttributeNames.GOAL_RESPONSE_DTO, goalResponseDto);
      return ViewNames.GOAL_DETAIL_PAGE;
    }
  }

  // 目標の編集ページを表示する
  @GetMapping(ViewNames.GOAL_EDIT_BY_ID)
  public String showGoalForm(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    int userId = customUserDetails.getId();
    GoalRequestDto goalRequestDto = goalService.prepareRequestDto(id, userId);

    if(goalRequestDto == null) {
      return RedirectUtil.redirectView(ViewNames.ERROR_NOT_ACCESSIBLE);
    }else{
      model.addAttribute(AttributeNames.GOAL_REQUEST_DTO, goalRequestDto);
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.prepareTagListByUserId(userId));

      return ViewNames.GOAL_EDIT_PAGE;
    }
  }

  // アクセス不可時のエラーページを表示
  @GetMapping(ViewNames.ERROR_NOT_ACCESSIBLE)
  public String showNotAccessibleView(){
    return ViewNames.ERROR_NOT_ACCESSIBLE;
  }

  // DTOのアノテーションで制御できないバリデーションを確認
  @InitBinder(AttributeNames.GOAL_REQUEST_DTO)
  public void initBinder(WebDataBinder dataBinder){
    dataBinder.addValidators(goalValidator);
  }

  /**
   * 入力されたデータにバインディングエラーが発生した場合はタグリストを詰め直して
   * 編集ページに戻り、そうでない場合はDBにデータを保存（登録・更新）し、
   * 詳細ページに遷移して保存の種類に応じたサクセスメッセージを表示する。
   *
   * @param id  目標ID
   * @param dto 目標のデータオブジェクト
   * @param result  バインディングの結果
   * @param model データをViewに渡すオブジェクト
   * @param customUserDetails カスタムユーザー情報
   * @param redirectAttributes  リダイレクト後に一度だけ表示するデータをViewに渡すインターフェース
   * @return  目標・詳細ページ
   */
  @PostMapping(ViewNames.GOAL_EDIT_BY_ID)
  public String editGoal(
      @PathVariable int id,
      @Valid @ModelAttribute(AttributeNames.GOAL_REQUEST_DTO) GoalRequestDto dto,
      BindingResult result, Model model,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      RedirectAttributes redirectAttributes) {

    int userId = customUserDetails.getId();

    if(result.hasErrors()){
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST, tagService.prepareTagListByUserId(userId));
      return ViewNames.GOAL_EDIT_PAGE;
    }

    SaveResult saveResult = goalService.saveGoal(userId, dto);

    boolean isRegistration = id == 0;
    if(isRegistration){
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.REGISTER_SUCCESS));
      redirectAttributes.addFlashAttribute(
          AttributeNames.ACHIEVEMENT_POPUP,
          messageUtil.getMessage(MessageKeys.GOAL_AWESOME));
    }else{
      redirectAttributes.addFlashAttribute(
          AttributeNames.SUCCESS_MESSAGE,
          messageUtil.getMessage(MessageKeys.UPDATE_SUCCESS));

      if(saveResult.isUpdatedStatus()){
        redirectAttributes.addFlashAttribute(
            AttributeNames.ACHIEVEMENT_POPUP,
            messageUtil.getMessage(MessageKeys.ACHIEVED_AWESOME));
      }
    }

    return RedirectUtil.redirectView(ViewNames.GOAL_DETAIL_PAGE, saveResult.id());
  }

  // 指定のIDの目標を削除して一覧ページにリダイレクトする。
  @DeleteMapping(ViewNames.GOAL_DETAIL_BY_ID)
  public String deleteGoal(
      @PathVariable int id, RedirectAttributes redirectAttributes) {

    goalService.deleteGoal(id);

    redirectAttributes.addFlashAttribute(
        AttributeNames.SUCCESS_MESSAGE,
        messageUtil.getMessage(MessageKeys.DELETE_SUCCESS));

    return RedirectUtil.redirectView(ViewNames.GOAL_PAGE);
  }
}
