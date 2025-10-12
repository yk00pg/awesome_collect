package webapp.AwesomeCollect.repository.user;

import java.util.List;
import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.entity.user.UserProgress;
import webapp.AwesomeCollect.mapper.user.UserProgressMapper;
import webapp.AwesomeCollect.provider.param.ExpiredUserParams;

/**
 * ユーザー進捗状況のリポジトリクラス。
 */
@Repository
public class UserProgressRepository {

  private final UserProgressMapper mapper;

  public UserProgressRepository(UserProgressMapper mapper){
    this.mapper = mapper;
  }

  public List<Integer> searchExpiredUserIdByUserId(ExpiredUserParams params){
    return mapper.selectExpiredUserIdByUserIdList(params);
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

  public void deleteUserProgressByUserId(int userId){
    mapper.deleteUserProgressByUserId(userId);
  }
}
