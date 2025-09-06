package webapp.AwesomeCollect.service.action;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.dto.action.DoneRequestDto;
import webapp.AwesomeCollect.dto.action.DoneResponseDto;
import webapp.AwesomeCollect.mapper.action.DailyDoneMapper;
import webapp.AwesomeCollect.entity.action.DailyDone;
import webapp.AwesomeCollect.repository.DailyDoneRepository;
import webapp.AwesomeCollect.service.TagService;
import webapp.AwesomeCollect.service.junction.DoneTagJunctionService;

@Service
public class DailyDoneService {

  private final DailyDoneMapper mapper;
  private final DailyDoneRepository dailyDoneRepository;
  private final DoneTagJunctionService doneTagJunctionService;
  private final TagService tagService;

  public DailyDoneService(DailyDoneMapper mapper, DailyDoneRepository dailyDoneRepository,
      DoneTagJunctionService doneTagJunctionService, TagService tagService){
    this.mapper = mapper;
    this.dailyDoneRepository = dailyDoneRepository;
    this.doneTagJunctionService = doneTagJunctionService;
    this.tagService = tagService;
  }

  // DBの登録状況に応じた閲覧用データオブジェクトを返す
  public DoneResponseDto prepareResponseDto(int userId, LocalDate date){
    List<DailyDone> dailyDoneList = dailyDoneRepository.searchDailyDone(userId, date);

    if(dailyDoneList == null || dailyDoneList.isEmpty()){
      return DoneResponseDto.createBlankDto(date);
    }else{
      List<List<String>> tagNamesList = new ArrayList<>();
      for(DailyDone done : dailyDoneList){
        List<Integer> tagIdList = doneTagJunctionService.searchTagIdsByActionId(done.getId());
        tagNamesList.add(
            tagIdList == null || tagIdList.isEmpty()
                ? Collections.emptyList()
                : tagService.prepareTagListByTagIdList(tagIdList));
      }

      DoneResponseDto dto = DoneResponseDto.fromDailyDone(dailyDoneList);
      dto.setTagsList(tagNamesList);
      return dto;
    }
  }

  // DBの登録状況に応じた編集用データオブジェクトを返す
  public DoneRequestDto prepareRequestDto(int userId, LocalDate date){
    List<DailyDone> dailyDoneList = dailyDoneRepository.searchDailyDone(userId, date);

    if(dailyDoneList == null || dailyDoneList.isEmpty()){
      return DoneRequestDto.createBlankDto(date);
    }else{
      return DoneRequestDto.fromDailyDone(dailyDoneList);
    }
  }

  public int countDailyDone(int userId){
    return mapper.countDailyDone(userId);
  }

  public void registerDailyDone(DailyDone done){
    mapper.insertDone(done);
  }

  public void updateDailyDone(DailyDone done){
    mapper.updateDone(done);
  }

  public void deleteDailyDoneById(int id){
    mapper.deleteDoneById(id);
  }

  public void deleteDailyDoneByDate(int userId, LocalDate date){
    mapper.deleteDoneByDate(userId, date);
  }
}
