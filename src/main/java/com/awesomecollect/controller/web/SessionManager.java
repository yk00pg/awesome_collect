package com.awesomecollect.controller.web;

import com.awesomecollect.common.constant.SessionKey;
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

  public SessionManager(HttpSession httpSession) {
    this.httpSession = httpSession;
  }

  public void enableCachedAwesomePoints() {
    httpSession.setAttribute(SessionKey.HAS_CACHED_AWESOME_POINTS, true);
  }

  public void disableCachedAwesomePoints() {
    httpSession.setAttribute(SessionKey.HAS_CACHED_AWESOME_POINTS, false);
  }

  public boolean hasCachedAwesomePoints() {
    return Boolean.TRUE.equals(
        httpSession.getAttribute(SessionKey.HAS_CACHED_AWESOME_POINTS));
  }

  public void enableCachedLearningDays() {
    httpSession.setAttribute(SessionKey.HAS_CACHED_LEARNING_DAYS, true);
  }

  public void disableCachedLearningDays() {
    httpSession.setAttribute(SessionKey.HAS_CACHED_LEARNING_DAYS, false);
  }

  public boolean hasCachedLearningDays() {
    return Boolean.TRUE.equals(
        httpSession.getAttribute(SessionKey.HAS_CACHED_LEARNING_DAYS));
  }

  public void enableCachedLearningTime() {
    httpSession.setAttribute(SessionKey.HAS_CACHED_LEARNING_TIME, true);
  }

  public void disableCachedLearningTime() {
    httpSession.setAttribute(SessionKey.HAS_CACHED_LEARNING_TIME, false);
  }

  public boolean hasCachedLearningTime() {
    return Boolean.TRUE.equals(
        httpSession.getAttribute(SessionKey.HAS_CACHED_LEARNING_TIME));
  }

  public void setAwesomePointDto(AwesomePointDto awesomePointDto) {
    httpSession.setAttribute(SessionKey.AWESOME_POINT_DTO, awesomePointDto);
  }

  public AwesomePointDto getCachedAwesomePointDto() {
    return (AwesomePointDto) httpSession.getAttribute(SessionKey.AWESOME_POINT_DTO);
  }

  public void setLearningTimeDto(LearningTimeDto learningTimeDto) {
    httpSession.setAttribute(SessionKey.LEARNING_TIME_DTO, learningTimeDto);
  }

  public LearningTimeDto getCachedLearningTimeDto() {
    return (LearningTimeDto) httpSession.getAttribute(SessionKey.LEARNING_TIME_DTO);
  }

  public void setLearningDaysDto(LearningDaysDto learningDaysDto) {
    httpSession.setAttribute(SessionKey.LEARNING_DAYS_DTO, learningDaysDto);
  }

  public LearningDaysDto getCachedLearningDaysDto() {
    return (LearningDaysDto) httpSession.getAttribute(SessionKey.LEARNING_DAYS_DTO);
  }

}
