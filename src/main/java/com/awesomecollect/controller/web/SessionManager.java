package com.awesomecollect.controller.web;

import com.awesomecollect.dto.dashboard.AwesomePointDto;
import com.awesomecollect.dto.dashboard.LearningDaysDto;
import com.awesomecollect.dto.dashboard.LearningTimeDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

/**
 * セッション情報を管理するクラス。
 */
@Component
public class SessionManager {

  private final HttpSession httpSession;

  private static final String HAS_UPDATED_RECORD_COUNT = "hasUpdatedRecordCount";
  private static final String HAS_UPDATED_LEARNING_DAYS = "hasUpdatedLearningDays";
  private static final String HAS_UPDATED_TIME = "hasUpdatedTime";
  private static final String AWESOME_POINT_DTO = "awesomePointDto";
  private static final String LEARNING_TIME_DTO = "learningTimeDto";
  private static final String LEARNING_DAYS_DTO = "learningDaysDto";

  public SessionManager(HttpSession httpSession) {
    this.httpSession = httpSession;
  }

  public void setHasUpdatedRecordCount(boolean value) {
    httpSession.setAttribute(HAS_UPDATED_RECORD_COUNT, value);
  }

  public Boolean hasUpdatedRecordCount() {
    return (Boolean) httpSession.getAttribute(HAS_UPDATED_RECORD_COUNT);
  }

  public void setHasUpdatedLearningDays(boolean value) {
    httpSession.setAttribute(HAS_UPDATED_LEARNING_DAYS, value);
  }

  public Boolean hasUpdatedLearningDays() {
    return (Boolean) httpSession.getAttribute(HAS_UPDATED_LEARNING_DAYS);
  }

  public void setHasUpdateTime(boolean value) {
    httpSession.setAttribute(HAS_UPDATED_TIME, value);
  }

  public Boolean hasUpdatedTime() {
    return (Boolean) httpSession.getAttribute(HAS_UPDATED_TIME);
  }

  public void setAwesomePointDto(AwesomePointDto awesomePointDto) {
    httpSession.setAttribute(AWESOME_POINT_DTO, awesomePointDto);
  }

  public AwesomePointDto getCachedAwesomePointDto() {
    return (AwesomePointDto) httpSession.getAttribute(AWESOME_POINT_DTO);
  }

  public void setLearningTimeDto(LearningTimeDto learningTimeDto) {
    httpSession.setAttribute(LEARNING_TIME_DTO, learningTimeDto);
  }

  public LearningTimeDto getCachedLearningTimeDto() {
    return (LearningTimeDto) httpSession.getAttribute(LEARNING_TIME_DTO);
  }

  public void setLearningDaysDto(LearningDaysDto learningDaysDto) {
    httpSession.setAttribute(LEARNING_DAYS_DTO, learningDaysDto);
  }

  public LearningDaysDto getCachedLearningDaysDto() {
    return (LearningDaysDto) httpSession.getAttribute(LEARNING_DAYS_DTO);
  }

}
