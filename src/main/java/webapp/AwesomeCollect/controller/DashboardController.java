package webapp.AwesomeCollect.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import webapp.AwesomeCollect.analysis.DashboardHelper;
import webapp.AwesomeCollect.dto.DashboardDto;
import webapp.AwesomeCollect.security.CustomUserDetails;

@Controller
public class DashboardController {

  private final DashboardHelper dashboardHelper;

  public DashboardController(DashboardHelper dashboardHelper){

    this.dashboardHelper = dashboardHelper;
  }

  @GetMapping(value = "/dashboard")
  public String showDashboard(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      HttpSession session, Model model){

    int userId = customUserDetails.getId();
    DashboardDto dto = dashboardHelper.prepareDashboardData(userId, session);

    model.addAttribute("totalAwesome", dto.getTotalAwesome());
    model.addAttribute("totalHours", dto.getTotalHours());
    model.addAttribute("dailyHoursList", dto.getDailyHoursList());
    model.addAttribute("monthlyHoursList", dto.getMonthlyHoursList());
    model.addAttribute("totalHoursByTag", dto.getTotalHoursByTag());

    return "/dashboard";
  }

  @GetMapping(value = "/dashboard/tag-hours/all")
  public String showAllTagHours(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      HttpSession session, Model model){

    int userId = customUserDetails.getId();
    DashboardDto dto = dashboardHelper.prepareAllTagHoursView(session, userId);

    model.addAttribute("totalHoursByAllTag", dto.getTotalHoursByTag());

    return "/dashboard/tag-hours/all";
  }
}
