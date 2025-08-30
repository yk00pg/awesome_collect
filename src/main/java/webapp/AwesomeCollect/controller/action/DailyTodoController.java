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
import webapp.AwesomeCollect.common.ActionViewPreparator;
import webapp.AwesomeCollect.dto.action.DailyTodoDto;
import webapp.AwesomeCollect.entity.action.DailyTodo;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.UserProgressService;
import webapp.AwesomeCollect.service.action.DailyTodoService;

@Controller
public class DailyTodoController {

  private final DailyTodoService dailyTodoService;
  private final ActionViewPreparator actionViewPreparator;
  private final UserProgressService userProgressService;

  public DailyTodoController(
      DailyTodoService dailyTodoService, ActionViewPreparator actionViewPreparator,
      UserProgressService userProgressService){

    this.dailyTodoService = dailyTodoService;
    this.userProgressService = userProgressService;
    this.actionViewPreparator = actionViewPreparator;
  }

  @GetMapping(value = "/todo/{date}")
  public String showDailyTodo(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      DailyTodoDto dto, Model model){

    showDailyTodoView(date, customUserDetails, dto, model);

    return "/todo";
  }

  @GetMapping(value = "/todo/edit/{date}")
  public String showDailyTodoForm(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      DailyTodoDto dto, Model model){

    showDailyTodoView(date, customUserDetails, dto, model);

    return "/todo/edit";
  }

  @GetMapping(value = "/todo")
  public String redirectByDate(@RequestParam LocalDate date) {
    return "redirect:/todo/" + date;
  }

  public void showDailyTodoView(
      LocalDate date, CustomUserDetails customUserDetails, DailyTodoDto dto, Model model) {

    int userId = customUserDetails.getId();
    List<DailyTodo> dailyTodoList = dailyTodoService.searchDailyTodo(userId, date);

    if(dailyTodoList.isEmpty()) {
      actionViewPreparator.prepareBlankTodoView(date, dto, model);
    }else{
      actionViewPreparator.prepareCurrentTodoView(date, dto, model, dailyTodoList);
    }
  }

  @PostMapping(value = "/todo/edit/{date}")
  public String editDailyTodo(
      @PathVariable LocalDate date,
      @Valid @ModelAttribute("dailyTodoDto") DailyTodoDto dto, BindingResult result,
      @AuthenticationPrincipal CustomUserDetails customUserDetails, HttpSession httpSession){

    // TODO: 最終的にはJSで空欄チェックをして弾くようにしたい
    List<String> contents = dto.getContentList();
    if (contents == null || contents.isEmpty() || contents.getFirst().trim().isEmpty()) {
      result.rejectValue("contents", "content.empty", "内容をひとつ以上入力してください");
    }

    int userId = customUserDetails.getId();
    List<Integer> ids = dto.getIdList();
    int loopSize = Math.min(ids.size(), contents.size());

    for (int i = 0; i < loopSize; i++) {
      int id = ids.get(i);
      if (id == 0) {
        if (dto.isContentEmpty(i)) {
          continue;
        }
        dailyTodoService.registerDailyTodo(dto.toDailyTodo(i, userId));
        if(i == 0) {
          userProgressService.updateUserProgress(userId);
        }
        httpSession.setAttribute("hasNewRecord", true);
      } else {
        if (dto.isContentEmpty(i)) {
          dailyTodoService.deleteDailyTodoById(id);
          httpSession.setAttribute("hasNewRecord", true);
        } else {
          dailyTodoService.updateDailyTodo(dto.toDailyTodoWithId(i, userId));
        }
      }
    }
    return "redirect:/todo/" + date;
  }

  @DeleteMapping(value = "/todo/{date}")
  public String deleteTodo(
      @PathVariable LocalDate date,
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      HttpSession httpSession){

    int userId = customUserDetails.getId();
    dailyTodoService.deleteDailyTodoByDate(userId, date);
    httpSession.setAttribute("hasNewRecord", true);

    return "redirect:/todo/" + date;
  }
}
