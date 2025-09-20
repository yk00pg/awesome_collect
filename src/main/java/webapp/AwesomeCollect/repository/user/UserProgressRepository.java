package webapp.AwesomeCollect.repository.user;

import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.entity.user.UserProgress;
import webapp.AwesomeCollect.mapper.user.UserProgressMapper;

@Repository
public class UserProgressRepository {

  private final UserProgressMapper mapper;

  public UserProgressRepository(UserProgressMapper mapper){
    this.mapper = mapper;
  }

  public UserProgress findUserProgressByUserId(int userId){
    return mapper.selectUserProgress(userId);
  }

  public void registerUserProgress(UserProgress userProgress) {
    mapper.insertUserProgress(userProgress);
  }

  public void updateUserProgress(UserProgress userProgress){
    mapper.updateUserProgress(userProgress);
  }

}
