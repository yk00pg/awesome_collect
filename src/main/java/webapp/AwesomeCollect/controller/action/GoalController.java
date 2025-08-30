package webapp.AwesomeCollect.controller.action;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import webapp.AwesomeCollect.common.util.JsonConverter;
import webapp.AwesomeCollect.common.TaggingManager;
import webapp.AwesomeCollect.common.ActionViewPreparator;
import webapp.AwesomeCollect.dto.action.GoalDto;
import webapp.AwesomeCollect.entity.action.Goal;
import webapp.AwesomeCollect.entity.junction.GoalTagJunction;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.UserProgressService;
import webapp.AwesomeCollect.service.action.GoalService;
import webapp.AwesomeCollect.service.junction.GoalTagJunctionService;
import webapp.AwesomeCollect.service.TagService;

@Controller
public class GoalController {

  private final GoalService goalService;
  private final TagService tagService;
  private final ActionViewPreparator actionViewPreparator;
  private final GoalTagJunctionService goalTagJunctionService;
  private final TaggingManager taggingManager;
  private final UserProgressService userProgressService;

  public GoalController(
      GoalService goalService, TagService tagService, ActionViewPreparator actionViewPreparator,
      GoalTagJunctionService goalTagJunctionService, TaggingManager taggingManager,
      UserProgressService userProgressService){

    this.goalService = goalService;
    this.tagService = tagService;
    this.actionViewPreparator = actionViewPreparator;
    this.goalTagJunctionService = goalTagJunctionService;
    this.taggingManager = taggingManager;
    this.userProgressService = userProgressService;
  }

  /**
   * 「目標」の一覧画面を表示する。<br>
   * ログイン中のユーザーのユーザーIDを取得し、ユーザーIDを基にDBから目標リストを取得する。<br>
   * 目標リストが空（未登録）の場合は空のデータオブジェクトをモデルに追加し、
   * そうでない場合は登録済みデータを入れたデータオブジェクトをモデルに追加する。
   *
   * @param customUserDetails ログイン中のユーザー情報
   * @param dto 「目標」のデータオブジェクト
   * @param model HTMLにデータを渡すオブジェクト
   * @return  goal.html
   */
  @GetMapping(value = "/goal")
  public String showGoal(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      GoalDto dto, Model model){

    int userId = customUserDetails.getId();
    List<Goal> goalList = goalService.searchGoal(userId);
    if(!goalList.isEmpty()){
      actionViewPreparator.prepareCurrentDataListView(
          dto, model, goalList, goalTagJunctionService);
    }
    return "/goal";
  }

  /**
   * ID別の「目標」の詳細画面を表示する。
   *
   * @param id  目標ID
   * @param customUserDetails ログイン中のユーザー情報
   * @param dto 「目標」のデータオブジェクト
   * @param model HTMLにデータを渡すオブジェクト
   * @return  goal/detail.html
   * @throws AccessDeniedException 登録していないIDのページを開こうとした場合
   */
  @GetMapping(value = "/goal/detail/{id}")
  public String showGoalDetail(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      GoalDto dto, Model model) throws AccessDeniedException {

    String result = showGoalDetailView(id, customUserDetails, dto, model);
    if(result != null){
      return result;
    }
    return "/goal/detail";
  }

  /**
   * ID別の「目標」の編集画面を表示する。
   *
   * @param id  目標ID
   * @param customUserDetails ログイン中のユーザー情報
   * @param dto 「目標」のデータオブジェクト
   * @param model HTMLにデータを渡すオブジェクト
   * @return  goal/detail/edit.html
   * @throws AccessDeniedException 登録していないIDのページを開こうとした場合
   */
  @GetMapping(value = "/goal/detail/edit/{id}")
  public String showGoalForm(
      @PathVariable int id,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      GoalDto dto, Model model) throws AccessDeniedException {

    String result = showGoalDetailView(id, customUserDetails, dto, model);
    if(result != null){
      return result;
    }
    return "/goal/detail/edit";
  }

  /**
   * ログイン中のユーザーのユーザーIDを取得し、ユーザーIDを基にDBからタグリストを取得する。<br>
   * 目標IDが0（新規登録）の場合は空のデータオブジェクトとタグリストをモデルに追加し、
   * そうでない場合は、目標IDとユーザーIDを基にDBを検索し、レコードがない場合は例外処理をする。<br>
   * レコートがある場合は登録済みデータを入れたデータオブジェクトとタグリストをモデルに追加する。
   *
   * @param id  目標ID
   * @param customUserDetails ログイン中のユーザー情報
   * @param dto 「目標」のデータオブジェクト
   * @param model HTMLにデータを渡すオブジェクト
   * @throws AccessDeniedException  登録していないIDのページを開こうとした場合
   */
  private String showGoalDetailView(
      int id, CustomUserDetails customUserDetails,
      GoalDto dto, Model model) throws AccessDeniedException {

    int userId = customUserDetails.getId();
    List<String> tagNameList = tagService.getTagNameList(userId);

    if(id == 0){
      actionViewPreparator.prepareBlankDoneView(id, dto, model, tagNameList);
      return null;
    }else {
      Goal goal = goalService.findGoalByIds(id,userId);
      if (goal == null) {
        return "redirect:/error/not-accessible";
      } else {
        actionViewPreparator.prepareCurrentDataView(
            dto, model, goal, tagNameList, goalTagJunctionService);
        return null;
      }
    }
  }

  @GetMapping(value = "/error/not-accessible")
  public String showNotAccessibleView(){
    return "/error/not-accessible";
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
  @PostMapping(value = "/goal/detail/edit/{id}")
  public String editGoal(
      @PathVariable int id,
      @Valid @ModelAttribute("goalDto") GoalDto dto, BindingResult result,
      @AuthenticationPrincipal CustomUserDetails customUserDetails, HttpSession httpSession) {

    List<String> pureTagList = JsonConverter.extractValues(dto.getTags());

    int userId = customUserDetails.getId();
    if(id == 0){
      if(dto.isContentEmpty()){
        result.rejectValue("content", "content.empty", "内容を入力してください");
      }else {
        Goal goal = dto.toGoal(userId);
        goalService.registerGoal(goal);
        id = goal.getId();
        taggingManager.resolveTagsAndRelations(
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
