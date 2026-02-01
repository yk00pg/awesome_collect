package com.awesomecollect.controller;

import com.awesomecollect.common.constant.AttributeNames;
import com.awesomecollect.common.constant.MappingValues;
import com.awesomecollect.common.constant.TemplateNames;
import com.awesomecollect.controller.web.SessionManager;
import com.awesomecollect.dto.dashboard.AwesomePointDto;
import com.awesomecollect.dto.dashboard.DashboardDto;
import com.awesomecollect.dto.dashboard.LearningDaysDto;
import com.awesomecollect.dto.dashboard.LearningTimeDto;
import com.awesomecollect.security.CustomUserDetails;
import com.awesomecollect.service.dashboard.AwesomeCountService;
import com.awesomecollect.service.dashboard.LearningDaysService;
import com.awesomecollect.service.dashboard.LearningTimeService;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ダッシュボードページのコントローラークラス。
 */
@Controller
public class DashboardController {

  private final AwesomeCountService awesomeCountService;
  private final LearningDaysService learningDaysService;
  private final LearningTimeService learningTimeService;
  private final SessionManager sessionManager;

  public DashboardController(
      AwesomeCountService awesomeCountService, LearningDaysService learningDaysService,
      LearningTimeService learningTimeService, SessionManager sessionManager){

    this.awesomeCountService = awesomeCountService;
    this.learningDaysService = learningDaysService;
    this.learningTimeService = learningTimeService;
    this.sessionManager = sessionManager;
  }

  // ダッシュボードのトップページを表示する。
  @GetMapping(MappingValues.DASHBOARD)
  public String showDashboard(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    int userId = customUserDetails.getId();

    AwesomePointDto awesomePointDto = getAwesomePointDto(userId);
    LearningDaysDto learningDaysDto = getLearningDaysDto(userId);
    LearningTimeDto learningTimeDto = getLearningTimeDto(userId);

    model.addAttribute(AttributeNames.DASHBOARD_DTO,
        new DashboardDto(awesomePointDto, learningDaysDto, learningTimeDto));

    return TemplateNames.DASHBOARD;
  }

  // 学習時間グラフページを表示する。
  @GetMapping(MappingValues.DASHBOARD_LEARNING_TIME_CHART)
  public String showLearningTimeChart(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    LearningTimeDto learningTimeDto = getLearningTimeDto(customUserDetails.getId());
    model.addAttribute(AttributeNames.LEARNING_TIME_DTO, learningTimeDto);

    return TemplateNames.DASHBOARD_LEARNING_TIME_CHART;
  }

  // 全タグ別学習時間グラフページを表示する。
  @GetMapping(MappingValues.DASHBOARD_ALL_TAG_TIME_CHART)
  public String showAllTagsTimeChart(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      Model model){

    LearningTimeDto learningTimeDto = getLearningTimeDto(customUserDetails.getId());
    model.addAttribute(
        AttributeNames.TAG_TOTAL_TIME_LIST, learningTimeDto.getTagTotalTimeList());

    return TemplateNames.DASHBOARD_ALL_TAG_TIME_CHART;
  }

  // セッション情報をServiceに渡してえらいポイントのDTOを受け取り、内容に変更があればセッションを更新する。
  private @Nullable AwesomePointDto getAwesomePointDto(int userId) {
    Boolean hasNewRecord = sessionManager.hasUpdatedRecordCount();
    AwesomePointDto cachedAwesomePointDto = sessionManager.getCachedAwesomePointDto();

    AwesomePointDto awesomePointDto =
        awesomeCountService.prepareAwesomePointDto(
            userId, hasNewRecord, cachedAwesomePointDto);

    if(!Objects.equals(cachedAwesomePointDto, awesomePointDto)) {
      sessionManager.setHasUpdatedRecordCount(false);
      sessionManager.setAwesomePointDto(awesomePointDto);
    }
    return awesomePointDto;
  }

  // セッション情報をServiceに渡して学習日数のDTOを受け取り、内容に変更があればセッションを更新する。
  private @Nullable LearningDaysDto getLearningDaysDto(int userId) {
    Boolean hasUpdatedLearnedDate = sessionManager.hasUpdatedLearningDays();
    LearningDaysDto cachedLearningDaysDto = sessionManager.getCachedLearningDaysDto();

    LearningDaysDto learningDaysDto =
        learningDaysService.prepareLearningDaysDto(
            userId, hasUpdatedLearnedDate, cachedLearningDaysDto);

    if(!Objects.equals(cachedLearningDaysDto, learningDaysDto)){
      sessionManager.setLearningDaysDto(learningDaysDto);
      sessionManager.setHasUpdatedLearningDays(false);
    }
    return learningDaysDto;
  }

  // セッション情報をServiceに渡して学習時間のDTOを受け取り、内容に変更があればセッションを更新する。
  private @NotNull LearningTimeDto getLearningTimeDto(int userId) {
    Boolean hasUpdatedTime = sessionManager.hasUpdatedTime();
    LearningTimeDto cachedLearningTimeDto = sessionManager.getCachedLearningTimeDto();

    LearningTimeDto learningTimeDto =
        learningTimeService.prepareLearningTimeDto(
            userId, hasUpdatedTime, cachedLearningTimeDto);

    if(!Objects.equals(cachedLearningTimeDto, learningTimeDto)){
      sessionManager.setLearningTimeDto(learningTimeDto);
      sessionManager.setHasUpdateTime(false);
    }

    return learningTimeDto;
  }
}
