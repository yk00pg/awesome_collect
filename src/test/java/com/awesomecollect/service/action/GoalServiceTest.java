package com.awesomecollect.service.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.awesomecollect.common.SaveResult;
import com.awesomecollect.dto.action.request.GoalRequestDto;
import com.awesomecollect.entity.action.Goal;
import com.awesomecollect.entity.junction.GoalTagJunction;
import com.awesomecollect.exception.DuplicateException;
import com.awesomecollect.repository.action.GoalRepository;
import com.awesomecollect.service.TagService;
import com.awesomecollect.service.junction.GoalTagJunctionService;
import com.awesomecollect.service.user.UserProgressService;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/*
    NOTE:
    1.新規登録の場合のSaveResult.id()の検証は、
      registerGoal(Goal)のDB副作用（IDの自動採番）の再現が必要なため、一時保留。
      Mockito の doAnswer を用いる必要があると思われるので、その理解を深めてから実装に挑戦する！
 */
@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

  @Mock
  GoalRepository goalRepository;

  @Mock
  GoalTagJunctionService goalTagJunctionService;

  @Mock
  TagService tagService;

  @Mock
  UserProgressService userProgressService;

  private final Clock clock = Clock.fixed(
      Instant.parse("2026-02-11T03:00:00Z"), // JST 12:00
      ZoneId.of("Asia/Tokyo")
  );

  private GoalService goalService;

  @BeforeEach
  void setUp() {
    goalService = new GoalService(
        goalRepository, goalTagJunctionService, tagService, userProgressService, clock);
  }

  private static final int USER_ID = 1;
  private static final String GOAL_TITLE = "エンジニアになる";
  private static final String GOAL_CONTENT = "2026年4月までにエンジニアデビューする！";
  private static final String ACHIEVED = "achieved";
  private static final String DOING = "doing";
  private static final String TAGS = "[{\"value\":\"#Java\"},{\"value\":\"#Webアプリ開発\"},{\"value\":\"#個人開発\"}]";
  private static final List<String> PURE_TAG_LIST = List.of("#Java","#Webアプリ開発","#個人開発");
  private static final List<Integer> TAG_ID_LIST = List.of(1,2,3);
  private static final LocalDateTime NOW_TIME =
      LocalDateTime.of(2026,2,11,12,0);

  @Test
  void saveGoal_目標IDが0でタイトルが重複していない場合は登録処理を行う() {
    when(tagService.resolveTagIdList(USER_ID, PURE_TAG_LIST))
        .thenReturn(TAG_ID_LIST);
    when(goalRepository.findIdByUserIdAndTitle(USER_ID, GOAL_TITLE))
        .thenReturn(null);

    GoalRequestDto dto = createDtoSample(0, DOING);
    SaveResult saveResult = goalService.saveGoal(USER_ID, dto);

    verify(tagService).resolveTagIdList(USER_ID, PURE_TAG_LIST);
    verify(goalRepository).findIdByUserIdAndTitle(USER_ID, GOAL_TITLE);

    ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
    verify(goalRepository).registerGoal(goalCaptor.capture());
    Goal captureGoal = goalCaptor.getValue();
    assertEquals(
        USER_ID,
        captureGoal.getUserId(),
        "DTOがGoalに変換され、ユーザーIDが代入される");
    assertEquals(
        GOAL_TITLE,
        captureGoal.getTitle(),
        "DTOがGoalに変換され、タイトルが代入される");
    assertEquals(
        GOAL_CONTENT,
        captureGoal.getContent(),
        "DTOがGoalに変換され、内容が代入される");
    assertEquals(
        NOW_TIME,
        captureGoal.getRegisteredAt(),
        "DTOがGoalに変換され、登録日時に現在日時が代入される");

    @SuppressWarnings("unchecked")
    ArgumentCaptor<BiFunction<Integer, Integer, GoalTagJunction>> biFunctionCaptor =
        ArgumentCaptor.forClass(BiFunction.class);

    verify(goalTagJunctionService).registerNewRelations(
        eq(captureGoal.getId()), biFunctionCaptor.capture(), eq(TAG_ID_LIST));
    verify(userProgressService).updateUserProgress(USER_ID);

    assertFalse(
        saveResult.isUpdatedStatus(),
        "新規登録のため、戻り値のステータス更新フラグにfalseが代入される");
  }

  @Test
  void saveGoal_目標IDが0でタイトルが重複している場合は例外を投げる() {
    when(goalRepository.findIdByUserIdAndTitle(USER_ID, GOAL_TITLE))
        .thenReturn(1);

    GoalRequestDto dto = createDtoSample(0, DOING);
    assertThrows(DuplicateException.class, () -> goalService.saveGoal(USER_ID, dto));

    verify(tagService).resolveTagIdList(USER_ID, PURE_TAG_LIST);
    verify(goalRepository).findIdByUserIdAndTitle(USER_ID, GOAL_TITLE);

    verifyNoMoreInteractions(goalRepository);
    verifyNoInteractions(goalTagJunctionService);
    verifyNoInteractions(userProgressService);
  }

  @Test
  void saveGoal_目標IDが0以外でタイトルが重複していない場合は更新処理を行う() {
    when(tagService.resolveTagIdList(USER_ID, PURE_TAG_LIST))
        .thenReturn(TAG_ID_LIST);
    when(goalRepository.findIdByUserIdAndTitle(USER_ID, GOAL_TITLE))
        .thenReturn(1);

    GoalRequestDto dto = createDtoSample(1, DOING);
    when(goalRepository.findGoalByIds(dto.getId(),USER_ID))
        .thenReturn(createOptGoal());

    SaveResult saveResult = goalService.saveGoal(USER_ID, dto);

    verify(tagService).resolveTagIdList(USER_ID, PURE_TAG_LIST);

    ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
    verify(goalRepository).updateGoal(goalCaptor.capture());
    Goal captureGoal = goalCaptor.getValue();
    assertEquals(
        USER_ID,
        captureGoal.getUserId(),
        "DTOがGoalに変換され、ユーザーIDが代入される");
    assertEquals(
        GOAL_TITLE,
        captureGoal.getTitle(),
        "DTOがGoalに変換され、タイトルが代入される");
    assertEquals(
        GOAL_CONTENT,
        captureGoal.getContent(),
        "DTOがGoalに変換され、内容が代入される");
    assertEquals(
        NOW_TIME,
        captureGoal.getUpdatedAt(),
        "DTOがGoalに変換され、更新日時に現在日時が代入される");
    assertNull(
        captureGoal.getStatusUpdatedAt(),
        "進捗状況が「取組中」のため、ステータス更新日時はnullのまま");

    @SuppressWarnings("unchecked")
    ArgumentCaptor<BiFunction<Integer, Integer, GoalTagJunction>> biFunctionCaptor =
        ArgumentCaptor.forClass(BiFunction.class);

    verify(goalTagJunctionService).updateRelations(
        eq(captureGoal.getId()), biFunctionCaptor.capture(), eq(TAG_ID_LIST));

    assertEquals(
        dto.getId(),
        saveResult.id(),
        "戻り値のidに目標IDが代入される");
    assertFalse(
        saveResult.isUpdatedStatus(),
        "進捗状況が「取組中」のため、戻り値のステータス更新フラグにfalseが代入される");
  }

  @Test
  void saveGoal_目標IDが0以外でタイトルが重複している場合は例外を投げる() {
    when(goalRepository.findIdByUserIdAndTitle(USER_ID, GOAL_TITLE))
        .thenReturn(2);

    GoalRequestDto dto = createDtoSample(1, DOING);
    assertThrows(DuplicateException.class, () -> goalService.saveGoal(USER_ID, dto));

    verify(tagService).resolveTagIdList(USER_ID, PURE_TAG_LIST);
    verify(goalRepository).findIdByUserIdAndTitle(USER_ID, GOAL_TITLE);

    verifyNoMoreInteractions(goalRepository);
    verifyNoInteractions(goalTagJunctionService);
  }

  @Test
  void saveGoal_目標IDが0以外で進捗状況が達成に更新された場合はステータス更新日時を設定する() {
    GoalRequestDto dto = createDtoSample(1, ACHIEVED);
    when(goalRepository.findGoalByIds(dto.getId(),USER_ID))
        .thenReturn(createOptGoal());
    when(goalRepository.findIdByUserIdAndTitle(USER_ID, GOAL_TITLE))
        .thenReturn(1);

    SaveResult saveResult = goalService.saveGoal(USER_ID, dto);

    ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
    verify(goalRepository).updateGoal(goalCaptor.capture());
    Goal captureGoal = goalCaptor.getValue();
    assertEquals(
        NOW_TIME,
        captureGoal.getStatusUpdatedAt(),
        "DTOがGoalに変換され、ステータス更新日時に現在日時が代入される");

    assertEquals(
        dto.getId(),
        saveResult.id(),
        "戻り値のidに目標IDが代入される");
    assertTrue(
        saveResult.isUpdatedStatus(),
        "進捗状況が「達成」に更新されたため、戻り値のステータス更新フラグにtrueが代入される");
  }

  private GoalRequestDto createDtoSample(int goalId,String status) {
    GoalRequestDto dto = new GoalRequestDto();
    dto.setId(goalId);
    dto.setTitle(GOAL_TITLE);
    dto.setContent(GOAL_CONTENT);
    dto.setStatus(status);
    dto.setTags(TAGS);
    return dto;
  }

  private Optional<Goal> createOptGoal() {
    Goal goal = new Goal();
    goal.setId(1);
    goal.setUserId(USER_ID);
    goal.setAchieved(false);
    return Optional.of(goal);
  }
}