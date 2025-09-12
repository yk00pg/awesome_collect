package webapp.AwesomeCollect.service.action;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.dto.action.GoalRequestDto;
import webapp.AwesomeCollect.dto.action.GoalResponseDto;
import webapp.AwesomeCollect.mapper.action.GoalMapper;
import webapp.AwesomeCollect.entity.action.Goal;
import webapp.AwesomeCollect.repository.GoalRepository;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.junction.GoalTagJunctionService;

@Service
public class GoalService {

  private final GoalRepository goalRepository;
  private final TagService tagService;
  private final GoalTagJunctionService goalTagJunctionService;
  private final GoalMapper mapper;

  public GoalService(
      GoalRepository goalRepository, TagService tagService,
      GoalTagJunctionService goalTagJunctionService,
      GoalMapper mapper){
    this.goalRepository = goalRepository;
    this.tagService = tagService;
    this.goalTagJunctionService = goalTagJunctionService;
    this.mapper = mapper;
  }

  public List<GoalResponseDto> prepareResponseDtoList(int userId){
    List<Goal> goalList = goalRepository.searchGoal(userId);
    if(goalList == null || goalList.isEmpty()){
      return new ArrayList<>();
    }else{
      List<GoalResponseDto> dtoList = new ArrayList<>();
      for(Goal goal : goalList){
        List<Integer> tagIdList =
            goalTagJunctionService.prepareTagIdListByActionId(goal.getId());
        List<String> tagNameList =
            tagService.prepareTagNameListByTagIdList(tagIdList);

        GoalResponseDto dto = GoalResponseDto.fromEntity(goal);
        dto.setTagList(tagNameList);
        dtoList.add(dto);
      }
      return dtoList;
    }
  }

  public GoalResponseDto prepareResponseDto(int goalId, int userId) {
    Goal goal = goalRepository.findGoalByIds(goalId, userId);
    if(goal == null){
      return null;
    }else{
      List<Integer> tagIdList =
          goalTagJunctionService.prepareTagIdListByActionId(goalId);
      List<String> tagNameList =
          tagService.prepareTagNameListByTagIdList(tagIdList);

      GoalResponseDto dto = GoalResponseDto.fromEntityWithId(goal);
      dto.setTagList(tagNameList);
      return dto;
    }
  }

  public GoalRequestDto prepareRequestDto(int goalId, int userId){
    Goal goal = goalRepository.findGoalByIds(goalId, userId);
    if(goal == null){
      return null;
    }else{
      List<Integer> tagIdList =
          goalTagJunctionService.prepareTagIdListByActionId(goalId);

      String tagName = tagService.prepareCombinedTagName(tagIdList);

      GoalRequestDto dto = GoalRequestDto.fromEntityWithId(goal);
      dto.setTags(tagName);
      return dto;
    }
  }

  public int countGoal(int userId){
    return mapper.countGoal(userId);
  }

  public int countAchieved(int userId){
    return mapper.countAchieved(userId);
  }

  public void registerGoal(Goal goal){
    mapper.insertGoal(goal);
  }

  public void updateGoal(Goal goal){
    mapper.updateGoal(goal);
  }

  public void deleteGoal(int id){
    mapper.deleteGoal(id);
  }
}
