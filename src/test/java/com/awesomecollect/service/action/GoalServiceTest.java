package com.awesomecollect.service.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.awesomecollect.common.SaveResult;
import com.awesomecollect.dto.action.request.GoalRequestDto;
import com.awesomecollect.dto.action.response.GoalResponseDto;
import com.awesomecollect.dto.dummy.DummyGoalDto;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
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
  private static final String COMBINED_TAG_NAME = "#Java, #Webアプリ開発, #個人開発";
  private static final List<Integer> TAG_ID_LIST = List.of(1,2,3);
  private static final LocalDateTime NOW_TIME =
      LocalDateTime.of(2026,2,11,12,0);

  @Test
  void prepareResponseDtoListForList_ユーザーIDを基にDTOリストを用意する(){
    when(goalRepository.searchGoal(USER_ID))
        .thenReturn(createGoalListSample());
    when(goalTagJunctionService.prepareTagIdListByActionId(anyInt()))
        .thenReturn(TAG_ID_LIST);
    when(tagService.prepareTagNameListByTagIdList(TAG_ID_LIST))
        .thenReturn(PURE_TAG_LIST);

    List<GoalResponseDto> dtoList = goalService.prepareResponseDtoListForList(USER_ID);

    verify(goalRepository).searchGoal(USER_ID);
    verify(goalTagJunctionService, times(dtoList.size()))
        .prepareTagIdListByActionId(anyInt());
    verify(tagService, times(dtoList.size()))
        .prepareTagNameListByTagIdList(TAG_ID_LIST);

    assertNotNull(dtoList);
    assertFalse(dtoList.isEmpty());
    for (GoalResponseDto dto : dtoList) {
      assertEquals(
          GOAL_TITLE,
          dto.getTitle(),
          "タイトルがDTOに代入される。"
      );
      assertEquals(
          DOING,
          dto.getStatus(),
          "進捗状況がDTOに代入される。"
      );
      assertEquals(
          PURE_TAG_LIST,
          dto.getTagList(),
          "TagServiceを呼び出して取得したタグ名リストがDTOに代入される。"
      );
    }
  }

  @Test
  void prepareResponseDtoListForList_レコードがない場合は空のリストを用意する(){
    when(goalRepository.searchGoal(USER_ID))
        .thenReturn(Collections.emptyList());

    List<GoalResponseDto> dtoList = goalService.prepareResponseDtoListForList(USER_ID);

    assertEquals(
        Collections.emptyList(),
        dtoList,
        "レコードがない（DBから空リストを取得した）場合は、空のリストを返す。");

    verifyNoInteractions(goalTagJunctionService);
    verifyNoInteractions(tagService);
  }

  @Test
  void prepareResponseDtoForDetails_ユーザーIDを基にDTOを返す(){
    int goalId = 1;
    when(goalRepository.findGoalByIds(goalId, USER_ID))
        .thenReturn(createOptGoal(goalId));
    when(goalTagJunctionService.prepareTagIdListByActionId(goalId))
        .thenReturn(TAG_ID_LIST);
    when(tagService.prepareTagNameListByTagIdList(TAG_ID_LIST))
        .thenReturn(PURE_TAG_LIST);

    GoalResponseDto dto = goalService.prepareResponseDtoForDetails(goalId, USER_ID);

    verify(goalRepository).findGoalByIds(goalId, USER_ID);
    verify(goalTagJunctionService).prepareTagIdListByActionId(goalId);
    verify(tagService).prepareTagNameListByTagIdList(TAG_ID_LIST);

    assertNotNull(dto);
    assertEquals(
        GOAL_TITLE,
        dto.getTitle(),
        "タイトルがDTOに代入される。"
    );
    assertEquals(
        GOAL_CONTENT,
        dto.getContent(),
        "内容がDTOに代入される。"
    );
    assertEquals(
        DOING,
        dto.getStatus(),
        "進捗状況がDTOに代入される。"
    );
    assertEquals(
        PURE_TAG_LIST,
        dto.getTagList(),
        "TagServiceを呼び出して取得したタグ名リストがDTOに代入される。"
    );
  }

  @Test
  void prepareResponseDtoForDetails_レコードがない場合はnullを返す(){
    int goalId = 1;
    when(goalRepository.findGoalByIds(goalId, USER_ID))
        .thenReturn(Optional.empty());

    GoalResponseDto dto = goalService.prepareResponseDtoForDetails(goalId, USER_ID);

    verify(goalRepository).findGoalByIds(goalId, USER_ID);
    verifyNoInteractions(goalTagJunctionService);
    verifyNoInteractions(tagService);

    assertNull(dto);
  }

  @Test
  void prepareRequestDtoForEdit_ユーザーIDを基にDTOを返す(){
    int goalId = 1;
    when(goalRepository.findGoalByIds(goalId, USER_ID))
        .thenReturn(createOptGoal(goalId));
    when(goalTagJunctionService.prepareTagIdListByActionId(goalId))
        .thenReturn(TAG_ID_LIST);
    when(tagService.prepareCombinedTagName(TAG_ID_LIST))
        .thenReturn(COMBINED_TAG_NAME);

    GoalRequestDto dto = goalService.prepareRequestDtoForEdit(goalId, USER_ID);

    verify(goalRepository).findGoalByIds(goalId, USER_ID);
    verify(goalTagJunctionService).prepareTagIdListByActionId(goalId);
    verify(tagService).prepareCombinedTagName(TAG_ID_LIST);

    assertNotNull(dto);
    assertEquals(
        GOAL_TITLE,
        dto.getTitle(),
        "タイトルがDTOに代入される。"
    );
    assertEquals(
        GOAL_CONTENT,
        dto.getContent(),
        "内容がDTOに代入される。"
    );
    assertEquals(
        DOING,
        dto.getStatus(),
        "進捗状況がDTOに代入される。"
    );
    assertEquals(
        COMBINED_TAG_NAME,
        dto.getTags(),
        "TagServiceを呼び出して取得した結合済みタグ名がDTOに代入される。"
    );
  }

  @Test
  void prepareRequestDtoForEdit_レコードがない場合はnullを返す(){
    int goalId = 1;
    when(goalRepository.findGoalByIds(goalId, USER_ID))
        .thenReturn(Optional.empty());

    GoalRequestDto dto = goalService.prepareRequestDtoForEdit(goalId, USER_ID);

    verify(goalRepository).findGoalByIds(goalId, USER_ID);
    verifyNoInteractions(goalTagJunctionService);
    verifyNoInteractions(tagService);

    assertNull(dto);
  }

  @Test
  void prepareRequestDtoForEdit_目標IDが0の場合は新規登録のため空のDTOを返す(){
    GoalRequestDto dto = goalService.prepareRequestDtoForEdit(0, USER_ID);
    assertNotNull(dto);
    assertNull(dto.getTitle());
    assertNull(dto.getContent());
    assertNull(dto.getStatus());
    assertNull(dto.getTags());

    verifyNoInteractions(goalRepository);
    verifyNoInteractions(goalTagJunctionService);
    verifyNoInteractions(tagService);
  }

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
        .thenReturn(createOptGoal(1));

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
    when(goalRepository.findIdByUserIdAndTitle(USER_ID, GOAL_TITLE))
        .thenReturn(1);

    GoalRequestDto dto = createDtoSample(1, ACHIEVED);
    when(goalRepository.findGoalByIds(dto.getId(),USER_ID))
        .thenReturn(createOptGoal(1));

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

  @Test
  void deleteGoal_目標IDを基に削除処理を実行する(){
    int goalId = 1;
    goalService.deleteGoal(goalId);

    InOrder inOrder = inOrder(goalTagJunctionService, goalRepository);
    inOrder.verify(goalTagJunctionService).deleteRelationByActionId(goalId);
    inOrder.verify(goalRepository).deleteGoal(goalId);
  }

  @Test
  void registerDummyGoal_ゲストユーザーIDとDTOリストを基にダミーデータを登録する(){
    when(tagService.resolveTagIdList(USER_ID, PURE_TAG_LIST))
        .thenReturn(TAG_ID_LIST);

    List<DummyGoalDto> dummyDtoList = createDummyDtoListSample();
    goalService.registerDummyGoal(USER_ID, dummyDtoList);

    verify(tagService, times(dummyDtoList.size()))
        .resolveTagIdList(USER_ID, PURE_TAG_LIST);

    ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
    verify(goalRepository,times(dummyDtoList.size()))
        .registerGoal(goalCaptor.capture());
    List<Goal> allCaptureGoal = goalCaptor.getAllValues();

    assertEquals(3, allCaptureGoal.size());

    for (Goal captureGoal : allCaptureGoal) {
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
    }

    @SuppressWarnings("unchecked")
    ArgumentCaptor<BiFunction<Integer, Integer, GoalTagJunction>> biFunctionCaptor =
        ArgumentCaptor.forClass(BiFunction.class);

    verify(goalTagJunctionService, times(dummyDtoList.size()))
        .registerNewRelations(
            anyInt(), biFunctionCaptor.capture(), eq(TAG_ID_LIST));
  }

  private List<Goal> createGoalListSample() {
    List<Goal> goalList = new ArrayList<>();
    for(int i = 1; i <= 3; i++){
      Goal goal = new Goal();
      goal.setId(i);
      goal.setUserId(USER_ID);
      goal.setTitle(GOAL_TITLE);
      goal.setAchieved(false);
      goalList.add(goal);
    }
    return goalList;
  }

  private Optional<Goal> createOptGoal(int goalId) {
    Goal goal = new Goal();
    goal.setId(goalId);
    goal.setUserId(USER_ID);
    goal.setTitle(GOAL_TITLE);
    goal.setContent(GOAL_CONTENT);
    goal.setAchieved(false);
    return Optional.of(goal);
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

  private List<DummyGoalDto> createDummyDtoListSample(){
    List<DummyGoalDto> dummyDtoList = new ArrayList<>();
    for(int i = 1; i <= 3; i++){
      DummyGoalDto  dummyDto = new DummyGoalDto();
      dummyDto.setTitle(GOAL_TITLE);
      dummyDto.setContent(GOAL_CONTENT);
      dummyDto.setStatus(ACHIEVED);
      dummyDto.setTagList(PURE_TAG_LIST);
      dummyDtoList.add(dummyDto);
    }
    return dummyDtoList;
  }
}