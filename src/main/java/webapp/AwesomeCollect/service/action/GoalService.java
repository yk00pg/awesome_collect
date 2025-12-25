package webapp.AwesomeCollect.service.action;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.common.SaveResult;
import webapp.AwesomeCollect.common.util.JsonConverter;
import webapp.AwesomeCollect.common.util.SessionManager;
import webapp.AwesomeCollect.dto.action.request.GoalRequestDto;
import webapp.AwesomeCollect.dto.action.response.GoalResponseDto;
import webapp.AwesomeCollect.dto.dummy.DummyGoalDto;
import webapp.AwesomeCollect.entity.action.Goal;
import webapp.AwesomeCollect.entity.junction.GoalTagJunction;
import webapp.AwesomeCollect.exception.DuplicateException;
import webapp.AwesomeCollect.exception.DuplicateType;
import webapp.AwesomeCollect.repository.action.GoalRepository;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.junction.GoalTagJunctionService;
import webapp.AwesomeCollect.service.user.UserProgressService;

/**
 * 目標のサービスクラス。
 */
@Service
public class GoalService {

  private final GoalRepository goalRepository;
  private final GoalTagJunctionService goalTagJunctionService;
  private final TagService tagService;
  private final UserProgressService userProgressService;
  private final SessionManager sessionManager;

  public GoalService(
      GoalRepository goalRepository, GoalTagJunctionService goalTagJunctionService,
      TagService tagService, UserProgressService userProgressService,
      SessionManager sessionManager) {

    this.goalRepository = goalRepository;
    this.goalTagJunctionService = goalTagJunctionService;
    this.tagService = tagService;
    this.userProgressService = userProgressService;
    this.sessionManager = sessionManager;
  }

  /**
   * ユーザーIDを基にDBを確認し、目標が登録されていない場合は空のリストを、
   * 登録されている場合は登録データを詰めた表示用データオブジェクトのリストを用意する。
   *
   * @param userId ユーザーID
   * @return 目標表示用データオブジェクト
   */
  @Transactional
  public List<GoalResponseDto> prepareResponseDtoList(int userId) {
    List<Goal> goalList = goalRepository.searchGoal(userId);
    if (goalList == null || goalList.isEmpty()) {
      return new ArrayList<>();
    } else {
      return assembleCurrentResponseDtoList(goalList);
    }
  }

  /**
   * 目標リストを一覧ページの表示用データオブジェクトに変換し、紐付けられたタグ名リストを設定する。
   *
   * @param goalList 目標リスト
   * @return 目標表示用データオブジェクトのリスト
   */
  private @NotNull List<GoalResponseDto> assembleCurrentResponseDtoList(
      List<Goal> goalList) {

    List<GoalResponseDto> dtoList = new ArrayList<>();
    for (Goal goal : goalList) {
      GoalResponseDto dto =
          assembleCurrentResponseDto(
              GoalResponseDto.fromEntityForList(goal), goal.getId());
      dtoList.add(dto);
    }
    return dtoList;
  }

  /**
   * 目標IDとユーザーIDを基にDBを確認し、目標が登録されていない場合はnullを返し、
   * 登録されている場合は登録データを詰めた表示用データオブジェクトを用意する。
   *
   * @param goalId 目標ID
   * @param userId ユーザーID
   * @return 目標表示用データオブジェクト
   */
  @Transactional
  public GoalResponseDto prepareResponseDto(int goalId, int userId) {
    Goal goal = goalRepository.findGoalByIds(goalId, userId);
    if (goal == null) {
      return null;
    } else {
      return assembleCurrentResponseDto(
          GoalResponseDto.fromEntityForDetail(goal), goalId);
    }
  }

  /**
   * 目標に紐付けられたタグ名リストを表示用データオブジェクトに設定する。
   *
   * @param dto    目標表示用データオブジェクト
   * @param goalId 目標ID
   * @return 目標表示用データオブジェクト
   */
  private @NotNull GoalResponseDto assembleCurrentResponseDto(
      GoalResponseDto dto, int goalId) {

    List<Integer> tagIdList =
        goalTagJunctionService.prepareTagIdListByActionId(goalId);
    List<String> tagNameList =
        tagService.prepareTagNameListByTagIdList(tagIdList);

    dto.setTagList(tagNameList);
    return dto;
  }

  /**
   * 目標IDが0の場合は空の入力用データオブジェクトを用意する。<br>
   * そうでない場合は、目標IDとユーザーIDを基にDBを確認し、目標が登録されていない場合は
   * nullを返し、登録されている場合は登録データを詰めた入力用データオブジェクトを用意する。
   *
   * @param goalId 目標ID
   * @param userId ユーザーID
   * @return 目標入力用データオブジェクト
   */
  @Transactional
  public GoalRequestDto prepareRequestDto(int goalId, int userId) {
    if (goalId == 0) {
      return new GoalRequestDto();
    }

    Goal goal = goalRepository.findGoalByIds(goalId, userId);
    if (goal == null) {
      return null;
    } else {
      return assembleCurrentRequestDto(goalId, goal);
    }
  }

  /**
   * 目標を入力用データオブジェクトに変換し、紐付けられたタグ名を設定する。
   *
   * @param goalId 目標ID
   * @param goal   目標
   * @return 目標入力用データオブジェクト
   */
  private @NotNull GoalRequestDto assembleCurrentRequestDto(int goalId, Goal goal) {
    List<Integer> tagIdList =
        goalTagJunctionService.prepareTagIdListByActionId(goalId);
    String tagName = tagService.prepareCombinedTagName(tagIdList);

    GoalRequestDto dto = GoalRequestDto.fromEntity(goal);
    dto.setTags(tagName);
    return dto;
  }

  /**
   * データの種類に応じてDBに保存（登録・更新）し、セッションのレコード数更新情報を変更する。
   *
   * @param userId ユーザーID
   * @param dto    目標入力用データオブジェクト
   * @return 保存結果オブジェクト
   */
  @Transactional
  public SaveResult saveGoal(int userId, GoalRequestDto dto) {
    List<String> pureTagList = JsonConverter.extractValues(dto.getTags());
    List<Integer> tagIdList = tagService.resolveTagIdList(userId, pureTagList);

    int goalId = dto.getId();
    SaveResult saveResult;
    if (goalId == 0) {
      saveResult = registerGoal(userId, dto, tagIdList);
    } else {
      saveResult = updateGoal(userId, dto, tagIdList, goalId);
    }

    sessionManager.setHasUpdatedRecordCount(true);

    return saveResult;
  }

  /**
   * DTOをエンティティに変換してDBに登録し、タグ情報を登録してユーザーの進捗情報を更新する。
   *
   * @param userId    ユーザーID
   * @param dto       目標入力用データオブジェクト
   * @param tagIdList タグIDリスト
   * @return 登録した目標ID
   * @throws DuplicateException 同ユーザーが同じタイトルをすでに登録している場合
   */
  private SaveResult registerGoal(
      int userId, GoalRequestDto dto, List<Integer> tagIdList)
      throws DuplicateException {

    if (isDuplicateTitle(dto.getId(), userId, dto.getTitle())) {
      throw new DuplicateException(DuplicateType.TITLE);
    }

    Goal goal = dto.toGoalForRegistration(userId);
    goalRepository.registerGoal(goal);
    goalTagJunctionService.registerNewRelations(
        goal.getId(), GoalTagJunction :: new, tagIdList);

    userProgressService.updateUserProgress(userId);

    return new SaveResult(goal.getId(), false);
  }

  /**
   * DTOをエンティティに変換してDBの目標、紐付けられたタグとの関係情報を更新する。
   *
   * @param userId    ユーザーID
   * @param dto       目標入力用データオブジェクト
   * @param tagIdList タグIDリスト
   * @param goalId    目標ID
   * @return 保存結果オブジェクト
   * @throws DuplicateException 同ユーザーが同じタイトルをすでに登録している場合
   */
  private SaveResult updateGoal(
      int userId, GoalRequestDto dto, List<Integer> tagIdList, int goalId)
      throws DuplicateException {

    if (isDuplicateTitle(goalId, userId, dto.getTitle())) {
      throw new DuplicateException(DuplicateType.TITLE);
    }

    Goal goal = dto.toGoalForUpdate(userId);
    boolean isAchievedUpdate = checkUpdatedStatus(userId, goalId, goal);

    goalRepository.updateGoal(goal);
    goalTagJunctionService.updateRelations(goalId, GoalTagJunction :: new, tagIdList);

    return new SaveResult(goalId, isAchievedUpdate);
  }

  // 目標タイトルが重複しているか確認する。
  private boolean isDuplicateTitle(int goalId, int userId, String title) {
    Integer recordId = goalRepository.findIdByUserIdAndTitle(userId, title.strip());
    return recordId != null && !recordId.equals(goalId);
  }

  // 進捗状況が更新されたか確認し、達成へ更新された場合はステータス更新日時を設定する。
  private boolean checkUpdatedStatus(int userId, int goalId, Goal goal) {
    boolean isAchievedUpdate = false;
    Goal currentGoal = goalRepository.findGoalByIds(goalId, userId);
    if (currentGoal.getUpdatedAt() == null
        && !currentGoal.isAchieved() && goal.isAchieved()) {

      goal.setStatusUpdatedAt(LocalDateTime.now());
      isAchievedUpdate = true;
    }
    return isAchievedUpdate;
  }

  /**
   * 指定のIDの目標、紐づけられたタグとの関係情報を削除し、
   * セッションのレコード数更新情報を変更する。
   *
   * @param goalId 目標ID
   */
  @Transactional
  public void deleteGoal(int goalId) {
    goalTagJunctionService.deleteRelationByActionId(goalId);
    goalRepository.deleteGoal(goalId);

    sessionManager.setHasUpdatedRecordCount(true);
  }

  /**
   * CSVファイルから読み込んだダミーデータをDBに登録し、セッションのレコード数更新情報を変更する。
   *
   * @param guestUserId ゲストユーザーID
   * @param recordList  CSVファイルから読み込んだレコードリスト
   */
  @Transactional
  public void registerDummyGoal(int guestUserId, List<DummyGoalDto> recordList){
    recordList.forEach(dto -> {
      Goal goal = dto.toEntity(guestUserId);
      goalRepository.registerGoal(goal);

      List<Integer> tagIdList =
          tagService.resolveTagIdList(guestUserId, dto.getTagList());
      goalTagJunctionService.registerNewRelations(
          goal.getId(), GoalTagJunction :: new, tagIdList);
    });

    sessionManager.setHasUpdatedRecordCount(true);
  }
}
