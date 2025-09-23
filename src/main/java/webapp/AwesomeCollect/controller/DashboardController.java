package webapp.AwesomeCollect.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.ViewNames;
import webapp.AwesomeCollect.dto.dashboard.AwesomePointDto;
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
  private final LearningDaysService learningDaysService;
  private final LearningTimeService learningTimeService;

  public DashboardController(
      AwesomeCountService awesomeCountService, LearningDaysService learningDaysService,
      LearningTimeService learningTimeService){

    this.awesomeCountService = awesomeCountService;
    this.learningDaysService = learningDaysService;
    this.learningTimeService = learningTimeService;
  }

  // ダッシュボードのトップページを表示する。
  @GetMapping(ViewNames.DASHBOARD_PAGE)
  public String showDashboard(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    int userId = customUserDetails.getId();
    AwesomePointDto awesomePointDto = awesomeCountService.prepareAwesomePointDto(userId);
    LearningDaysDto learningDaysDto = learningDaysService.prepareLearningDaysDto(userId);
    LearningTimeDto learningTimeDto = learningTimeService.prepareLearningTimeDto(userId);

    model.addAttribute(AttributeNames.DASHBOARD_DTO,
        new DashboardDto(awesomePointDto, learningDaysDto, learningTimeDto));

    return ViewNames.DASHBOARD_PAGE;
  }

  // 学習時間グラフページを表示する。
  @GetMapping(ViewNames.DASHBOARD_LEARNING_TIME_CHART)
  public String showLearningTimeChart(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    model.addAttribute(AttributeNames.LEARNING_TIME_DTO,
        learningTimeService.prepareLearningTimeDto(customUserDetails.getId()));

    return ViewNames.DASHBOARD_LEARNING_TIME_CHART;
  }

  // 全タグ別学習時間グラフページを表示する。
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
