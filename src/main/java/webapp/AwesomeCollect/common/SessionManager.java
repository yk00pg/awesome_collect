package webapp.AwesomeCollect.common;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import webapp.AwesomeCollect.dto.LearningTimeDto;

@Component
public class SessionManager {

  private final HttpSession httpSession;

  private static final String HAS_UPDATED_RECORD_COUNT = "hasUpdatedRecordCount";
  private static final String HAS_UPDATED_TIME = "hasUpdatedTime";
  private static final String CACHED_AWESOME = "cachedAwesome";
  private static final String LEARNING_TIME_DTO = "learningTimeDto";

  public SessionManager(HttpSession httpSession){
    this.httpSession = httpSession;
  }

  public void setHasUpdatedRecordCount(boolean value){
    httpSession.setAttribute(HAS_UPDATED_RECORD_COUNT, value);
  }

  public Boolean hasUpdatedRecordCount(){
    return (Boolean) httpSession.getAttribute(HAS_UPDATED_RECORD_COUNT);
  }

  public void setHasUpdateTime(boolean value){
    httpSession.setAttribute(HAS_UPDATED_TIME, value);
  }

  public Boolean hasUpdatedTime(){
    return (Boolean) httpSession.getAttribute(HAS_UPDATED_TIME);
  }

  public void setAwesomePoint(int awesomePoint){
    httpSession.setAttribute(CACHED_AWESOME, awesomePoint);
  }

  public Integer getCachedAwesomePoint(){
    return (Integer) httpSession.getAttribute(CACHED_AWESOME);
  }

  public void setLearningTimeDto(LearningTimeDto learningTimeDto){
    httpSession.setAttribute(LEARNING_TIME_DTO, learningTimeDto);
  }

  public LearningTimeDto getCachedLearningTimeDto(){
    return (LearningTimeDto) httpSession.getAttribute(LEARNING_TIME_DTO);
  }

}
