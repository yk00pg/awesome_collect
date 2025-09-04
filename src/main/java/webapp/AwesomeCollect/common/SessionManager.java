package webapp.AwesomeCollect.common;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {

  private final HttpSession httpSession;

  private static final String HAS_UPDATED_RECORD_COUNT = "hasUpdatedRecordCount";

  public SessionManager(HttpSession httpSession){
    this.httpSession = httpSession;
  }

  public void setHasUpdatedRecordCount(boolean value){
    httpSession.setAttribute(HAS_UPDATED_RECORD_COUNT, value);
  }

}
