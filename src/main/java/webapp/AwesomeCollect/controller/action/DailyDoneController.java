package webapp.AwesomeCollect.controller.action;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.ViewNames;
import webapp.AwesomeCollect.common.util.JsonConverter;
import webapp.AwesomeCollect.common.TaggingManager;
import webapp.AwesomeCollect.common.util.RedirectUtil;
import webapp.AwesomeCollect.dto.action.DoneRequestDto;
import webapp.AwesomeCollect.dto.action.DoneResponseDto;
import webapp.AwesomeCollect.dto.action.TodoResponseDto;
import webapp.AwesomeCollect.entity.action.DailyDone;
import webapp.AwesomeCollect.entity.junction.DoneTagJunction;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.UserProgressService;
import webapp.AwesomeCollect.service.action.DailyDoneService;
import webapp.AwesomeCollect.service.action.DailyTodoService;
import webapp.AwesomeCollect.service.junction.DoneTagJunctionService;

@Controller
public class DailyDoneController {

  private final DailyDoneService dailyDoneService;
  private final DailyTodoService dailyTodoService;
  private final DoneTagJunctionService doneTagJunctionService;
  private final TaggingManager taggingManager;
  private final UserProgressService userProgressService;

  public DailyDoneController(
      DailyDoneService dailyDoneService, DailyTodoService dailyTodoService,
      DoneTagJunctionService doneTagJunctionService,
      TaggingManager taggingManager, UserProgressService userProgressService){

    this.dailyDoneService = dailyDoneService;
    this.dailyTodoService = dailyTodoService;
    this.doneTagJunctionService = doneTagJunctionService;
    this.taggingManager = taggingManager;
    this.userProgressService = userProgressService;
  }

  // できたことリストの閲覧ページを表示
  @GetMapping(ViewNames.DAILY_DONE_VIEW_PAGE)
  public String showDailyDone(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    DoneResponseDto currentDto =
        dailyDoneService.prepareResponseDto(customUserDetails.getId(), date);
    model.addAttribute(AttributeNames.DONE_RESPONSE_DTO, currentDto);

    return ViewNames.DONE_PAGE;
  }

  // できたことリストの編集ページを表示
  @GetMapping(ViewNames.DAILY_DONE_EDIT_PAGE)
  public String showDailyDoneForm(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model) {

    TodoResponseDto currentTodoDto =
        dailyTodoService.prepareResponseDto(customUserDetails.getId(), date);
    DoneRequestDto currentDoneDto =
        dailyDoneService.prepareRequestDto(customUserDetails.getId(), date);

    model.addAttribute(AttributeNames.TODO_RESPONSE_DTO, currentTodoDto);
    model.addAttribute(AttributeNames.DONE_REQUEST_DTO, currentDoneDto);

    return ViewNames.DAILY_DONE_EDIT_PAGE;
  }

  // できたことリストの閲覧ページにリダイレクト
  @GetMapping(ViewNames.DONE_PAGE)
  public String redirectByDate(@RequestParam LocalDate date) {
    return RedirectUtil.redirectView(ViewNames.DONE_PAGE, date);
  }

  /**
   * 登録状況に応じて分岐し、入力された内容を登録/更新/削除する。<br>
   * データオブジェクトからコンテンツリストを取得し、空欄チェックを行い、学習時間の合計もチェックする。<br>
   * 入力されたハッシュタグ（JSON形式）を文字列に変換し、リスト形式で取得する。<br>
   * ログイン中のユーザーのユーザーIDを取得し、データオブジェクトからできたことのIDリストを取得する。<br>
   * 入力された内容の登録/更新/削除処理を行う。
   *
   * @param date  選択した日付
   * @param dto 「できたこと」のデータオブジェクト
   * @param result  データバインディングの結果
   * @param customUserDetails ログイン中のユーザー情報
   * @return  done/edit.html
   */
  @PostMapping(value = "/done/edit/{date}")
  public String editDailyTodo(
      @PathVariable LocalDate date,
      @Valid @ModelAttribute("dailyDoneDto") DoneRequestDto dto, BindingResult result,
      @AuthenticationPrincipal CustomUserDetails customUserDetails, Model model, HttpSession httpSession) {

    if(result.hasErrors()){
      String defaultMessage = result.getFieldError().getDefaultMessage();
      model.addAttribute("failureMessage", defaultMessage);
      return "redirect:/done/edit/" + date;
    }

    if(dto.isFutureDate()){
      result.rejectValue("date", "futureDate", "未来の成果を登録することはできません");
      return "redirect:/done/edit" + date;
    }

    // TODO: 最終的にはJSで空欄チェックをして弾くようにしたい
    if (!dto.isContentListEmpty()) {
      result.rejectValue("contentList", "content.empty", "内容をひとつ以上入力してください");
    }

    // TODO: JSでcontentが空欄でない場合はhoursも必須にするようにしたい
    // TODO: 最終的にはJSでリアルタイムで合計を出して弾くようにしたい
    if(!dto.isTotalHoursValid()){
      result.rejectValue("hoursList", "hours.total.exceeded", "合計が24時間を超えています");
    }

    int userId = customUserDetails.getId();
    List<Integer> idList = dto.getIdList();
    int loopSize = Math.min(idList.size(), dto.getContentList().size());

    List<List<String>> pureTagsList = JsonConverter.extractValues(dto.getTagsList());

    for (int i = 0; i < loopSize; i++) {
      int doneId = idList.get(i);
      List<String> pureTagList = pureTagsList.get(i);

      if (doneId == 0) {
        if (dto.isContentEmpty(i)) {
          continue;
        }
        DailyDone dailyDone = dto.toDailyDone(i, userId);
        dailyDoneService.registerDailyDone(dailyDone);
        taggingManager.resolveTagsAndRelations(
            dailyDone.getId(), pureTagList, userId, DoneTagJunction :: new, doneTagJunctionService);

        if(i == 0) {
          userProgressService.updateUserProgress(userId);
        }
        httpSession.setAttribute("hasNewRecord", true);
        httpSession.setAttribute("hasNewHours", true);

      } else {
        if (dto.isContentEmpty(i)) {
          dailyDoneService.deleteDailyDoneById(doneId);
          doneTagJunctionService.deleteRelationByActionId(doneId);
          httpSession.setAttribute("hasNewRecord", true);
          httpSession.setAttribute("hasNewHours", true);
        } else {
          dailyDoneService.updateDailyDone(dto.toDailyDoneWithId(i, userId));
          taggingManager.updateTagsAndRelations(
              doneId, pureTagList, userId, DoneTagJunction :: new, doneTagJunctionService);
          httpSession.setAttribute("hasNewHours", true);
        }
      }
    }
    return "redirect:/done/" + date;
  }

  @DeleteMapping(value = "/done/{date}")
  public String deleteDone(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      HttpSession httpSession){

    int userId = customUserDetails.getId();
    doneTagJunctionService.deleteRelationByDate(userId, date);
    dailyDoneService.deleteDailyDoneByDate(userId, date);
    httpSession.setAttribute("hasNewRecord", true);

    return "redirect:/done/" + date;
  }
}
