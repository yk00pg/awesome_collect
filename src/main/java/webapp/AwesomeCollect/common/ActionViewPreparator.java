package webapp.AwesomeCollect.common;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import webapp.AwesomeCollect.common.util.DateTimeFormatUtil;
import webapp.AwesomeCollect.dto.action.BaseActionDto;
import webapp.AwesomeCollect.dto.action.DoneRequestDto;
import webapp.AwesomeCollect.entity.action.DailyDone;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.junction.BaseActionTagJunctionService;
import webapp.AwesomeCollect.service.junction.DoneTagJunctionService;

@Component
public class ActionViewPreparator {

  private final TagService tagService;

  public ActionViewPreparator(TagService tagService){
    this.tagService = tagService;
  }

  public <E, D extends BaseActionDto<E,D>> void prepareBlankDoneView(
      int actionId, D dto,Model model, List<String> tagNameList){

    dto.setId(actionId);
    model.addAttribute("actionDto", dto);
    model.addAttribute("tagNameList", tagNameList);
  }

  public <E, D extends BaseActionDto<E, D>, T> void prepareCurrentDataListView(
      D dto, Model model, List<E> actionList, BaseActionTagJunctionService<T> junctionService){

    List<D> currentDtoList = dto.fromEntityList(actionList);
    for(D currentDto : currentDtoList){
      setCurrentTags(junctionService, currentDto);
    }
    model.addAttribute("actionDtoList", currentDtoList);
  }

  public <E, D extends BaseActionDto<E,D>, T> void prepareCurrentDataView(
      D dto, Model model, E actionEntity, List<String> tagNameList,
      BaseActionTagJunctionService<T> junctionService){

    D currentDto = dto.fromEntity(actionEntity);
    setCurrentTags(junctionService, currentDto);
    model.addAttribute("actionDto", currentDto);
    model.addAttribute("tagNameList", tagNameList);
  }

  private <E, D extends BaseActionDto<E, D>, T> void setCurrentTags(
      BaseActionTagJunctionService<T> junctionService, D currentDto) {

    List<Integer> tagIdList = junctionService.searchTagIdsByActionId(currentDto.getId());
    currentDto.setTags(tagService.getTagName(tagIdList));
  }

  public void prepareCurrentDoneView(
      LocalDate date, DoneRequestDto dto, Model model, List<DailyDone> dailyDoneList,
      List<String> tagNameList, DoneTagJunctionService doneTagJunctionService) {

    DoneRequestDto currentDto = dto.fromDailyDone(dailyDoneList);
    List<String> currentTagList = new ArrayList<>();
    List<Integer> doneIds = currentDto.getIdList();
    for(int doneId : doneIds){
      List<Integer> tagIdList = doneTagJunctionService.searchTagIdsByActionId(doneId);
      String currentTag = tagService.getTagName(tagIdList);
      currentTagList.add(currentTag == null ? "" : currentTag);
    }
    currentDto.setTagsList(currentTagList);

    model.addAttribute("dailyDoneDto", currentDto);
    model.addAttribute("hasDoneContent", true);
    model.addAttribute("prevDate", date.minusDays(1));
    model.addAttribute("nextDate", date.plusDays(1));
    model.addAttribute("formattedDate", DateTimeFormatUtil.formatDate(date));
    model.addAttribute("tagNameList", tagNameList);
  }
}
