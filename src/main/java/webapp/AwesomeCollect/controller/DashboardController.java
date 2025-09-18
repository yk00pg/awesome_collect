package webapp.AwesomeCollect.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.ViewNames;
import webapp.AwesomeCollect.dto.dashboard.DashboardDto;
import webapp.AwesomeCollect.dto.dashboard.LearningTimeDto;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.dashboard.AwesomeCountService;
import webapp.AwesomeCollect.service.dashboard.LearningTimeService;

/**
 * ダッシュボードページのコントローラークラス。
 */
@Controller
public class DashboardController {

  private final LearningTimeService learningTimeService;
  private final AwesomeCountService awesomeCountService;

  public DashboardController(
     LearningTimeService learningTimeService, AwesomeCountService awesomeCountService){

    this.learningTimeService = learningTimeService;
    this.awesomeCountService = awesomeCountService;
  }

  // ダッシュボードページを表示
  @GetMapping(ViewNames.DASHBOARD_PAGE)
  public String showDashboard(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    int userId = customUserDetails.getId();
    int totalAwesome = awesomeCountService.calculateTotalAwesome(userId);
    LearningTimeDto learningTimeDto = learningTimeService.prepareLearningTimeDto(userId);

    DashboardDto dashboardDto = new DashboardDto(totalAwesome, learningTimeDto);
    model.addAttribute(AttributeNames.DASHBOARD_DTO, dashboardDto);

    return ViewNames.DASHBOARD_PAGE;
  }

  // 全タグの学習時間ページを表示
  @GetMapping(ViewNames.DASHBOARD_ALL_TAG_TIME)
  public String showAllTagHours(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    LearningTimeDto learningTimeDto =
        learningTimeService.prepareLearningTimeDto(customUserDetails.getId());

    model.addAttribute(
        AttributeNames.TAG_TOTAL_TIME_LIST, learningTimeDto.getTagTotalTimeList());

    return ViewNames.DASHBOARD_ALL_TAG_TIME;
  }
}
