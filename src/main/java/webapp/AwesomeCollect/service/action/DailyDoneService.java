package webapp.AwesomeCollect.service.action;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.dto.action.DoneRequestDto;
import webapp.AwesomeCollect.dto.action.DoneResponseDto;
import webapp.AwesomeCollect.mapper.action.DailyDoneMapper;
import webapp.AwesomeCollect.entity.action.DailyDone;
import webapp.AwesomeCollect.repository.DailyDoneRepository;

@Service
public class DailyDoneService {

  private final DailyDoneMapper mapper;
  private final DailyDoneRepository dailyDoneRepository;

  public DailyDoneService(DailyDoneMapper mapper, DailyDoneRepository dailyDoneRepository){
    this.mapper = mapper;
    this.dailyDoneRepository = dailyDoneRepository;
  }

  // DBの登録状況に応じた閲覧用データオブジェクトを返す
  public DoneResponseDto prepareResponseDto(int userId, LocalDate date){
    List<DailyDone> dailyDoneList = dailyDoneRepository.searchDailyDone(userId, date);

    if(dailyDoneList == null || dailyDoneList.isEmpty()){
      return DoneResponseDto.createBlankDto(date);
    }else{
      return DoneResponseDto.fromDailyDone(dailyDoneList);
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
