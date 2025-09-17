package webapp.AwesomeCollect.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.ViewNames;
import webapp.AwesomeCollect.dto.DashboardDto;
import webapp.AwesomeCollect.security.CustomUserDetails;
import webapp.AwesomeCollect.service.LearningTimeService;

@Controller
public class DashboardController {

  private final LearningTimeService learningTimeService;

  public DashboardController(
     LearningTimeService learningTimeService){

    this.learningTimeService = learningTimeService;
  }

  @GetMapping(ViewNames.DASHBOARD_PAGE)
  public String showDashboard(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    DashboardDto dto = new DashboardDto();
    dto.setTotalAwesome(0);
    dto.setLearningTimeDto(
        learningTimeService.prepareLearningTimeDto(customUserDetails.getId()));

    model.addAttribute(AttributeNames.DASHBOARD_DTO, dto);

    return ViewNames.DASHBOARD_PAGE;
  }

  @GetMapping(ViewNames.DASHBOARD_ALL_TAG_TIME)
  public String showAllTagHours(Model model){

    model.addAttribute(
        AttributeNames.TAG_TOTAL_TIME_LIST, learningTimeService.prepareTagTotalTimeList());

    return ViewNames.DASHBOARD_ALL_TAG_TIME;
  }
}
