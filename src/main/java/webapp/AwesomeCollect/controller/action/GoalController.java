package webapp.AwesomeCollect.controller.action;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
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
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.ViewNames;
import webapp.AwesomeCollect.common.util.JsonConverter;
import webapp.AwesomeCollect.common.ActionViewPreparator;
import webapp.AwesomeCollect.common.util.RedirectUtil;
import webapp.AwesomeCollect.dto.action.GoalDto;
import webapp.AwesomeCollect.dto.action.GoalRequestDto;
import webapp.AwesomeCollect.dto.action.GoalResponseDto;
import webapp.AwesomeCollect.entity.action.Goal;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.UserProgressService;
import webapp.AwesomeCollect.service.action.GoalService;
import webapp.AwesomeCollect.service.junction.GoalTagJunctionService;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.validation.GoalValidator;

/**
 * 目標のコントローラークラス。
 */
@Controller
public class GoalController {

  private final GoalService goalService;
  private final GoalTagJunctionService goalTagJunctionService;
  private final TagService tagService;
  private final GoalValidator goalValidator;

  public GoalController(
      GoalService goalService, GoalTagJunctionService goalTagJunctionService,
      TagService tagService, GoalValidator goalValidator){

    this.goalService = goalService;
    this.goalTagJunctionService = goalTagJunctionService;
    this.tagService = tagService;
    this.goalValidator = goalValidator;
  }

  // 目標リストの閲覧ページを表示
  @GetMapping(ViewNames.GOAL_PAGE)
  public String showGoal(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    model.addAttribute(
        AttributeNames.GOAL_RESPONSE_DTO_LIST,
        goalService.prepareResponseDtoList(customUserDetails.getId()));

    return ViewNames.GOAL_PAGE;
  }

  // 目標の詳細ページを表示
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

  // 目標の編集ページを表示
  @GetMapping(ViewNames.GOAL_EDIT_BY_ID)
  public String showGoalForm(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    int userId = customUserDetails.getId();

    GoalRequestDto goalRequestDto =
        id == 0
            ? new GoalRequestDto()
            : goalService.prepareRequestDto(id, userId);

    if(goalRequestDto == null) {
      return RedirectUtil.redirectView(ViewNames.ERROR_NOT_ACCESSIBLE);
    }else{
      model.addAttribute(AttributeNames.GOAL_REQUEST_DTO, goalRequestDto);
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST,
          tagService.prepareTagListByUserId(userId));

      return ViewNames.GOAL_EDIT_PAGE;
    }
  }

  @GetMapping(ViewNames.ERROR_NOT_ACCESSIBLE)
  public String showNotAccessibleView(){
    return ViewNames.ERROR_NOT_ACCESSIBLE;
  }

  @InitBinder(AttributeNames.GOAL_REQUEST_DTO)
  public void initBinder(WebDataBinder dataBinder){
    dataBinder.addValidators(goalValidator);
  }

  /**
   * 登録状況に応じて分岐し、入力された内容を登録/更新/削除する。<br>
   * 入力されたハッシュタグ（JSON形式）を文字列に変換し、リスト形式で取得する。<br>
   * ログイン中のユーザーのユーザーIDを取得する。<br>
   * 目標IDが0（新規登録）の場合、内容が空であればデータバインディングの結果にエラーとして追加し、
   * そうでなければ目標とタグの登録処理を実行する。<br>
   * 目標IDが0以外（更新）の場合は、内容が空であれば削除処理を実行し、そうでなければ更新処理を実行する。
   *
   * @param id  目標ID
   * @param dto 「目標」のデータオブジェクト
   * @param result  データバインディングの結果
   * @param customUserDetails ログイン中のユーザー情報
   * @return  goal/detail/edit.html
   */
  @PostMapping(ViewNames.GOAL_EDIT_BY_ID)
  public String editGoal(
      @PathVariable int id,
      @Valid @ModelAttribute(AttributeNames.GOAL_REQUEST_DTO) GoalRequestDto dto,
      BindingResult result, Model model,
      @AuthenticationPrincipal CustomUserDetails customUserDetails, HttpSession httpSession) {

    if(result.hasErrors()){
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST,
          tagService.prepareTagListByUserId(customUserDetails.getId()));

      return ViewNames.GOAL_EDIT_PAGE;
    }

    List<String> pureTagList = JsonConverter.extractValues(dto.getTags());

    int userId = customUserDetails.getId();

    if(id == 0) {
      if (dto.isContentEmpty()) {
        result.rejectValue("content", "content.empty", "内容を入力してください");
      } else {
        Goal goal = dto.toGoal(userId);
        goalService.registerGoal(goal);
        id = goal.getId();
/*        taggingManager.resolveTagsAndRelations(
            id, pureTagList, userId, GoalTagJunction::new, goalTagJunctionService);
        userProgressService.updateUserProgress(userId);
        httpSession.setAttribute("hasNewRecord", true);
      }
    }else{ // TODO: 削除機能は内容の空欄判断ではなく、詳細画面で削除ボタンをつけるようにしたい
      if(!dto.isContentEmpty()){
        goalService.updateGoal(dto.toGoalWithId(userId));
        taggingManager.updateTagsAndRelations(
            id, pureTagList, userId, GoalTagJunction::new, goalTagJunctionService);
      }
    } */
      }
    }
    return "redirect:/goal/detail/" + id;
  }

  @DeleteMapping(value = "/goal/detail/{id}")
  public String deleteGoal(@PathVariable int id, HttpSession httpSession) {

    goalTagJunctionService.deleteRelationByActionId(id);
    goalService.deleteGoal(id);
    httpSession.setAttribute("hasNewRecord", true);

    return "redirect:/goal";
  }
}
