package webapp.AwesomeCollect.controller.action;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.constant.ViewNames;
import webapp.AwesomeCollect.common.util.JsonConverter;
import webapp.AwesomeCollect.common.TaggingManager;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.common.util.RedirectUtil;
import webapp.AwesomeCollect.dto.action.DoneRequestDto;
import webapp.AwesomeCollect.dto.action.DoneResponseDto;
import webapp.AwesomeCollect.dto.action.TodoResponseDto;
import webapp.AwesomeCollect.entity.action.DailyDone;
import webapp.AwesomeCollect.entity.junction.DoneTagJunction;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.UserProgressService;
import webapp.AwesomeCollect.service.action.DailyDoneService;
import webapp.AwesomeCollect.service.action.DailyTodoService;
import webapp.AwesomeCollect.service.junction.DoneTagJunctionService;
import webapp.AwesomeCollect.validation.DailyDoneValidator;

@Controller
public class DailyDoneController {

  private final DailyDoneService dailyDoneService;
  private final DailyTodoService dailyTodoService;
  private final TagService tagService;
  private final DailyDoneValidator dailyDoneValidator;
  private final MessageUtil messageUtil;
  private final DoneTagJunctionService doneTagJunctionService;

  public DailyDoneController(
      DailyDoneService dailyDoneService, DailyTodoService dailyTodoService,
      TagService tagService, DailyDoneValidator dailyDoneValidator,
      MessageUtil messageUtil, DoneTagJunctionService doneTagJunctionService){

    this.dailyDoneService = dailyDoneService;
    this.dailyTodoService = dailyTodoService;
    this.tagService = tagService;
    this.dailyDoneValidator = dailyDoneValidator;
    this.messageUtil = messageUtil;
    this.doneTagJunctionService = doneTagJunctionService;
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

    TodoResponseDto currentTodoDto =
        dailyTodoService.prepareResponseDto(userId, date);
    DoneRequestDto currentDoneDto =
        dailyDoneService.prepareRequestDto(userId, date);
    List<String> tagNameList = tagService.prepareTagListByUserId(userId);

    model.addAttribute(AttributeNames.TODO_RESPONSE_DTO, currentTodoDto);
    model.addAttribute(AttributeNames.DONE_REQUEST_DTO, currentDoneDto);
    model.addAttribute(AttributeNames.TAG_NAME_LIST, tagNameList);

    return ViewNames.DONE_EDIT_PAGE;
  }

  // DTOのアノテーションで制御できないバリデーションを確認
  @InitBinder(AttributeNames.DONE_REQUEST_DTO)
  public void initBinder(WebDataBinder dataBinder){
    dataBinder.addValidators(dailyDoneValidator);
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
  @PostMapping(ViewNames.DAILY_DONE_EDIT_PAGE)
  public String editDailyTodo(
      @PathVariable LocalDate date,
      @Valid @ModelAttribute(AttributeNames.DONE_REQUEST_DTO) DoneRequestDto dto,
      BindingResult result, Model model,
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {

    if(result.hasErrors()){
      // 参考用のやることリストとタグリストを詰め直す
      model.addAttribute(
          AttributeNames.TODO_RESPONSE_DTO,
          dailyTodoService.prepareResponseDto(customUserDetails.getId(), date));
      model.addAttribute(
          AttributeNames.TAG_NAME_LIST,
          tagService.prepareTagListByUserId(customUserDetails.getId()));

      return ViewNames.DONE_EDIT_PAGE;
    }

    dailyDoneService.saveDailyDone(customUserDetails.getId(), dto);

    return RedirectUtil.redirectView(ViewNames.DONE_PAGE, date);
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
