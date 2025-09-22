package webapp.AwesomeCollect.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.ViewNames;
import webapp.AwesomeCollect.dto.dashboard.DashboardDto;
import webapp.AwesomeCollect.dto.dashboard.LearningDaysDto;
import webapp.AwesomeCollect.dto.dashboard.LearningTimeDto;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.dashboard.AwesomeCountService;
import webapp.AwesomeCollect.service.dashboard.LearningDaysService;
import webapp.AwesomeCollect.service.dashboard.LearningTimeService;

/**
 * ダッシュボードページのコントローラークラス。
 */
@Controller
public class DashboardController {

  private final AwesomeCountService awesomeCountService;
  private final LearningTimeService learningTimeService;
  private final LearningDaysService learningDaysService;

  public DashboardController(
      AwesomeCountService awesomeCountService, LearningTimeService learningTimeService,
      LearningDaysService learningDaysService){

    this.awesomeCountService = awesomeCountService;
    this.learningTimeService = learningTimeService;
    this.learningDaysService = learningDaysService;
  }

  // ダッシュボードのトップページを表示する。
  @GetMapping(ViewNames.DASHBOARD_PAGE)
  public String showDashboard(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    int userId = customUserDetails.getId();
    int totalAwesome = awesomeCountService.calculateTotalAwesome(userId);
    LearningTimeDto learningTimeDto = learningTimeService.prepareLearningTimeDto(userId);
    LearningDaysDto learningDaysDto = learningDaysService.prepareLearningDaysDto(userId);

    DashboardDto dashboardDto =
        new DashboardDto(totalAwesome, learningTimeDto, learningDaysDto);
    model.addAttribute(AttributeNames.DASHBOARD_DTO, dashboardDto);

    return ViewNames.DASHBOARD_PAGE;
  }

  // 学習時間チャートページを表示する。
  @GetMapping(ViewNames.DASHBOARD_LEARNING_TIME_CHART)
  public String showLearningTimeChart(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    LearningTimeDto learningTimeDto =
        learningTimeService.prepareLearningTimeDto(customUserDetails.getId());
    model.addAttribute(AttributeNames.LEARNING_TIME_DTO, learningTimeDto);

    return ViewNames.DASHBOARD_LEARNING_TIME_CHART;
  }

  // 全タグ分の学習時間チャートページを表示する。
  @GetMapping(ViewNames.DASHBOARD_ALL_TAG_TIME_CHART)
  public String showAllTagsTimeChart(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    LearningTimeDto learningTimeDto =
        learningTimeService.prepareLearningTimeDto(customUserDetails.getId());
    model.addAttribute(
        AttributeNames.TAG_TOTAL_TIME_LIST, learningTimeDto.getTagTotalTimeList());

    return ViewNames.DASHBOARD_ALL_TAG_TIME_CHART;
  }
}
