package com.awesomecollect.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.awesomecollect.entity.user.UserProgress;
import com.awesomecollect.repository.user.UserProgressRepository;
import com.awesomecollect.service.BonusAwesomeService;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProgressServiceTest {

  @Mock
  UserProgressRepository userProgressRepository;

  @Mock
  BonusAwesomeService bonusAwesomeService;

  private final Clock clock = Clock.fixed(
      Instant.parse("2026-02-09T03:00:00Z"), // JST 12:00
      ZoneId.of("Asia/Tokyo")
  );

  private UserProgressService userProgressService;

  @BeforeEach
  void setUp() {
    userProgressService = new UserProgressService(
        userProgressRepository, bonusAwesomeService, clock);
  }

  private static final int USER_ID = 1;
  private static final int TOTAL_ACTION_DAYS = 6;
  private static final int CURRENT_STREAK = 6;
  private static final int LONGEST_STREAK = 6;
  private static final int STREAK_BONUS_COUNT = 10;
  
  private static final LocalDate TODAY = LocalDate.of(2026,2,9);
  private static final LocalDate YESTERDAY = LocalDate.of(2026,2,8);
  private static final LocalDate TWO_DAYS_AGO = LocalDate.of(2026,2,7);

  @Test
  void updateUserProgress_当日すでに更新済みの場合は処理がスキップされる() {
    UserProgress userProgress = createUserProgressSample(TODAY);

    when(userProgressRepository.findUserProgressByUserId(USER_ID))
        .thenReturn(userProgress);

    userProgressService.updateUserProgress(USER_ID);

    verifyNoInteractions(bonusAwesomeService);
    verify(userProgressRepository, never()).updateUserProgress(userProgress);
  }

  @Test
  void updateUserProgress_最終更新日が昨日の場合は日数と回数がそれぞれ加算される() {
    UserProgress userProgress = createUserProgressSample(YESTERDAY);

    when(userProgressRepository.findUserProgressByUserId(USER_ID))
        .thenReturn(userProgress);

    when(bonusAwesomeService.registerBonusAwesome(
      USER_ID, CURRENT_STREAK + 1, TODAY))
        .thenReturn(3);

    userProgressService.updateUserProgress(USER_ID);

    assertEquals(
        TOTAL_ACTION_DAYS + 1,
        userProgress.getTotalActionDays(),
        "累計記録日数に1日加算されること");

    assertEquals(
        TODAY,
        userProgress.getLastActionDate(),
        "最終記録日が今日になること");

    assertEquals(
        CURRENT_STREAK + 1,
        userProgress.getCurrentStreak(),
        "連続記録日数に1日加算されること");

    assertEquals(
        LONGEST_STREAK + 1,
        userProgress.getLongestStreak(),
        "最長連続記録日数に1日加算されること");

    assertEquals(
        STREAK_BONUS_COUNT + 3,
        userProgress.getStreakBonusCount(),
        "連続記録ボーナス獲得回数に3回加算されること");

    verify(bonusAwesomeService).registerBonusAwesome(
        USER_ID, userProgress.getCurrentStreak(), TODAY);
    verify(userProgressRepository).updateUserProgress(userProgress);
  }

  @Test
  void updateUserProgress_最終記録日が一昨日の場合は連続記録日数がリセットされてその他の日数と回数は加算される() {
    UserProgress userProgress = createUserProgressSample(TWO_DAYS_AGO);

    when(userProgressRepository.findUserProgressByUserId(USER_ID))
        .thenReturn(userProgress);

    when(bonusAwesomeService.registerBonusAwesome(
        USER_ID, 1, TODAY))
        .thenReturn(1);

    userProgressService.updateUserProgress(USER_ID);

    assertEquals(
        TOTAL_ACTION_DAYS + 1,
        userProgress.getTotalActionDays(),
        "累計記録日数に1日加算されること");

    assertEquals(
        TODAY,
        userProgress.getLastActionDate(),
        "最終記録日が今日になること");

    assertEquals(
        1,
        userProgress.getCurrentStreak(),
        "連続記録日数がリセットされて1になること");

    assertEquals(
        LONGEST_STREAK,
        userProgress.getLongestStreak(),
        "最長連続記録日数が変わらないこと");

    assertEquals(
        STREAK_BONUS_COUNT + 1,
        userProgress.getStreakBonusCount(),
        "連続記録ボーナス獲得回数に1回加算されること");

    verify(bonusAwesomeService).registerBonusAwesome(
        USER_ID, userProgress.getCurrentStreak(), TODAY);
    verify(userProgressRepository).updateUserProgress(userProgress);
  }

  @Test
  void updateUserProgress_初回記録時は日数と回数がそれぞれ1になる() {
    UserProgress userProgress = createNoRecordUserProgressSample();

    when(userProgressRepository.findUserProgressByUserId(USER_ID))
        .thenReturn(userProgress);

    when(bonusAwesomeService.registerBonusAwesome(
        USER_ID, 1, TODAY))
        .thenReturn(1);

    userProgressService.updateUserProgress(USER_ID);

    assertEquals(
        1,
        userProgress.getTotalActionDays(),
        "累計記録日数が1日になること");

    assertEquals(
        TODAY,
        userProgress.getLastActionDate(),
        "最終記録日が今日になること");

    assertEquals(
        1,
        userProgress.getCurrentStreak(),
        "連続記録日数が1日になること");

    assertEquals(
        1,
        userProgress.getLongestStreak(),
        "最長連続記録日数が1日になること");

    assertEquals(
        1,
        userProgress.getStreakBonusCount(),
        "連続記録ボーナス獲得回数が1回になること");

    verify(bonusAwesomeService).registerBonusAwesome(
        USER_ID, userProgress.getCurrentStreak(), TODAY);
    verify(userProgressRepository).updateUserProgress(userProgress);
  }

  @Test
  void updateUserProgress_最長記録日数が現在の連続記録日数より多い場合は更新されない(){
    UserProgress userProgress =
        createFewerCurrentStreakUserProgressSample(YESTERDAY);

    when(userProgressRepository.findUserProgressByUserId(USER_ID))
        .thenReturn(userProgress);

    userProgressService.updateUserProgress(USER_ID);

    assertEquals(
        10,
        userProgress.getLongestStreak(),
        "最長連続記録日数が変わらないこと");

    verify(bonusAwesomeService).registerBonusAwesome(
        USER_ID, userProgress.getCurrentStreak(), TODAY);
    verify(userProgressRepository).updateUserProgress(userProgress);
  }

  private UserProgress createUserProgressSample(LocalDate lastActionDate){
    UserProgress userProgress = new UserProgress();
    userProgress.setUserId(USER_ID);
    userProgress.setTotalActionDays(TOTAL_ACTION_DAYS);
    userProgress.setLastActionDate(lastActionDate);
    userProgress.setCurrentStreak(CURRENT_STREAK);
    userProgress.setLongestStreak(LONGEST_STREAK);
    userProgress.setStreakBonusCount(STREAK_BONUS_COUNT);
    return userProgress;
  }

  private UserProgress createNoRecordUserProgressSample(){
    UserProgress userProgress = new UserProgress();
    userProgress.setUserId(USER_ID);
    return userProgress;
  }

  private UserProgress createFewerCurrentStreakUserProgressSample(LocalDate lastActionDate){
    UserProgress userProgress = new UserProgress();
    userProgress.setUserId(USER_ID);
    userProgress.setTotalActionDays(TOTAL_ACTION_DAYS);
    userProgress.setLastActionDate(lastActionDate);
    userProgress.setCurrentStreak(2);
    userProgress.setLongestStreak(10);
    userProgress.setStreakBonusCount(STREAK_BONUS_COUNT);
    return userProgress;
  }
}