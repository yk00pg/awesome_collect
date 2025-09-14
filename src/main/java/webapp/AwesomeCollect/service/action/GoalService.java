package webapp.AwesomeCollect.service.action;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.SaveResult;
import webapp.AwesomeCollect.common.SessionManager;
import webapp.AwesomeCollect.common.util.JsonConverter;
import webapp.AwesomeCollect.dto.action.GoalRequestDto;
import webapp.AwesomeCollect.dto.action.GoalResponseDto;
import webapp.AwesomeCollect.entity.junction.GoalTagJunction;
import webapp.AwesomeCollect.entity.action.Goal;
import webapp.AwesomeCollect.repository.GoalRepository;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.UserProgressService;
import webapp.AwesomeCollect.service.junction.GoalTagJunctionService;

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
      SessionManager sessionManager){

    this.goalRepository = goalRepository;
    this.goalTagJunctionService = goalTagJunctionService;
    this.tagService = tagService;
    this.userProgressService = userProgressService;
    this.sessionManager = sessionManager;
  }

  // DBの登録状況に応じた表示用データオブジェクトをリスト形式で返す
  public List<GoalResponseDto> prepareResponseDtoList(int userId){
    List<Goal> goalList = goalRepository.searchGoal(userId);
    if(goalList == null || goalList.isEmpty()){
      return new ArrayList<>();
    }else{
      return assembleCurrentReponseDtoList(goalList);
    }
  }

  // DBの登録内容を基に表示用データオブジェクトを組み立てて返す
  private @NotNull List<GoalResponseDto> assembleCurrentReponseDtoList(
      List<Goal> goalList) {

    List<GoalResponseDto> dtoList = new ArrayList<>();
    for(Goal goal : goalList){
      GoalResponseDto dto = GoalResponseDto.fromEntityForList(goal);

      List<Integer> tagIdList =
          goalTagJunctionService.prepareTagIdListByActionId(goal.getId());
      List<String> tagNameList =
          tagService.prepareTagNameListByTagIdList(tagIdList);

      dto.setTagList(tagNameList);
      dtoList.add(dto);
    }
    return dtoList;
  }

  // DBの登録状況に応じた表示用データオブジェクトを返す
  public GoalResponseDto prepareResponseDto(int goalId, int userId) {
    Goal goal = goalRepository.findGoalByIds(goalId, userId);
    if(goal == null){
      return null;
    }else{
      return assembleCurrentResponseDto(goalId, goal);
    }
  }

  // DBの登録内容を基に表示用データオブジェクトを組み立てて返す
  private @NotNull GoalResponseDto assembleCurrentResponseDto(int goalId, Goal goal) {
    GoalResponseDto dto = GoalResponseDto.fromEntityForDetail(goal);

    List<Integer> tagIdList =
        goalTagJunctionService.prepareTagIdListByActionId(goalId);
    List<String> tagNameList =
        tagService.prepareTagNameListByTagIdList(tagIdList);

    dto.setTagList(tagNameList);
    return dto;
  }

  // DBの登録状況に応じた入力用データオブジェクトを返す
  public GoalRequestDto prepareRequestDto(int goalId, int userId){
    Goal goal = goalRepository.findGoalByIds(goalId, userId);
    if(goal == null){
      return null;
    }else{
      return assembleCurrentRequestDto(goalId, goal);
    }
  }

  // DBの登録内容を基に入力用データオブジェクトを組み立てて返す
  private @NotNull GoalRequestDto assembleCurrentRequestDto(int goalId, Goal goal) {
    GoalRequestDto dto = GoalRequestDto.fromEntity(goal);

    List<Integer> tagIdList =
        goalTagJunctionService.prepareTagIdListByActionId(goalId);
    String tagName = tagService.prepareCombinedTagName(tagIdList);

    dto.setTags(tagName);
    return dto;
  }

  /**
   * データの種類に応じてDBへの保存処理（登録・更新）を行う。
   *
   * @param userId  ユーザーID
   * @param dto 目標のデータオブジェクト
   * @return  保存結果オブジェクト
   */
  public SaveResult saveGoal(int userId, GoalRequestDto dto){
    List<String> pureTagList = JsonConverter.extractValues(dto.getTags());
    List<Integer> tagIdList = tagService.resolveTagIdList(userId, pureTagList);

    int id = dto.getId();
    SaveResult saveResult;
    if(id == 0){
      int goalId = registerGoal(userId, dto, tagIdList);
      saveResult = new SaveResult(goalId, false);
    }else{
      saveResult = updateGoal(userId, dto, tagIdList, id);
    }
    sessionManager.setHasUpdatedRecordCount(true);

    return saveResult;
  }

  /**
   * DTOをエンティティに変換してDBに登録し、タグ情報を処理する。<br>
   * セッション情報を変更し、ユーザーの進捗情報を更新する。
   *
   * @param userId  ユーザーID
   * @param dto 目標のデータオブジェクト
   * @param tagIdList タグIDリスト
   * @return  登録した目標ID
   */
  private int registerGoal(
      int userId, GoalRequestDto dto, List<Integer> tagIdList) {

    Goal goal = dto.toGoalForRegistration(userId);
    goalRepository.registerGoal(goal);

    if(tagIdList != null){
      goalTagJunctionService.registerNewRelations(
          goal.getId(), GoalTagJunction :: new, tagIdList);
    }
    sessionManager.setHasUpdatedRecordCount(true);
    userProgressService.updateUserProgress(userId);

    return goal.getId();
  }

  /**
   * DTOをエンティティに変換し、DBのレコード、タグ情報、セッション情報を更新する。
   *
   * @param userId  ユーザーID
   * @param dto 目標のデータオブジェクト
   * @param tagIdList タグIDリスト
   * @param id  目標ID
   * @return  保存結果オブジェクト
   */
  private SaveResult updateGoal(
      int userId, GoalRequestDto dto, List<Integer> tagIdList, int id) {

    Goal goal = dto.toGoalForUpdate(userId);
    // 進捗状況が更新されているか確認
    boolean isAchievedUpdate =
        !goalRepository.findGoalByIds(id, userId).isAchieved() && goal.isAchieved();

    goalRepository.updateGoal(goal);

    if(tagIdList != null){
      goalTagJunctionService.updateRelations(id, GoalTagJunction :: new, tagIdList);
    }
    sessionManager.setHasUpdatedRecordCount(true);

    return new SaveResult(id, isAchievedUpdate);
  }

  // 指定のIDの目標を削除
  public void deleteGoal(int id){
    goalTagJunctionService.deleteRelationByActionId(id);
    goalRepository.deleteGoal(id);

    sessionManager.setHasUpdatedRecordCount(true);
  }

  // 指定のユーザーのレコード数を取得
  public int countGoal(int userId){
    return goalRepository.countGoal(userId);
  }

  // 指定のユーザーの達成レコード数を取得
  public int countAchieved(int userId){
    return goalRepository.countAchieved(userId);
  }
}
