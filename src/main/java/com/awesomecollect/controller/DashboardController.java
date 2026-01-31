package com.awesomecollect.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.awesomecollect.common.constant.AttributeNames;
import com.awesomecollect.common.constant.MappingValues;
import com.awesomecollect.common.constant.TemplateNames;
import com.awesomecollect.dto.dashboard.AwesomePointDto;
import com.awesomecollect.dto.dashboard.DashboardDto;
import com.awesomecollect.dto.dashboard.LearningDaysDto;
import com.awesomecollect.dto.dashboard.LearningTimeDto;
import com.awesomecollect.security.CustomUserDetails;
import com.awesomecollect.service.dashboard.AwesomeCountService;
import com.awesomecollect.service.dashboard.LearningDaysService;
import com.awesomecollect.service.dashboard.LearningTimeService;

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
  @GetMapping(MappingValues.DASHBOARD)
  public String showDashboard(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    int userId = customUserDetails.getId();
    AwesomePointDto awesomePointDto = awesomeCountService.prepareAwesomePointDto(userId);
    LearningDaysDto learningDaysDto = learningDaysService.prepareLearningDaysDto(userId);
    LearningTimeDto learningTimeDto = learningTimeService.prepareLearningTimeDto(userId);

    model.addAttribute(AttributeNames.DASHBOARD_DTO,
        new DashboardDto(awesomePointDto, learningDaysDto, learningTimeDto));

    return TemplateNames.DASHBOARD;
  }

  // 学習時間グラフページを表示する。
  @GetMapping(MappingValues.DASHBOARD_LEARNING_TIME_CHART)
  public String showLearningTimeChart(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    model.addAttribute(AttributeNames.LEARNING_TIME_DTO,
        learningTimeService.prepareLearningTimeDto(customUserDetails.getId()));

    return TemplateNames.DASHBOARD_LEARNING_TIME_CHART;
  }

  // 全タグ別学習時間グラフページを表示する。
  @GetMapping(MappingValues.DASHBOARD_ALL_TAG_TIME_CHART)
  public String showAllTagsTimeChart(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    LearningTimeDto learningTimeDto =
        learningTimeService.prepareLearningTimeDto(customUserDetails.getId());
    model.addAttribute(
        AttributeNames.TAG_TOTAL_TIME_LIST, learningTimeDto.getTagTotalTimeList());

    return TemplateNames.DASHBOARD_ALL_TAG_TIME_CHART;
  }
}
