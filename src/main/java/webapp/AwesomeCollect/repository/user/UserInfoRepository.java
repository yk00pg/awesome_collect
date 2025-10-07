package webapp.AwesomeCollect.repository.user;

import org.springframework.stereotype.Repository;
import webapp.AwesomeCollect.entity.user.UserInfo;
import webapp.AwesomeCollect.mapper.user.UserInfoMapper;

/**
 * ユーザー情報のリポジトリクラス。
 */
@Repository
public class UserInfoRepository {

  private final UserInfoMapper mapper;

  public UserInfoRepository(UserInfoMapper mapper) {
    this.mapper = mapper;
  }

  public UserInfo findUserInfoByBinaryLoginId(String loginId){
    return mapper.findUserInfoByBinaryLoginId(loginId);
  }

  public UserInfo findUserInfoById(int id){
    return mapper.findUserInfoById(id);
  }

  public UserInfo findUserInfoByLoginId(String loginId){
    return mapper.findUserInfoByLoginId(loginId);
  }

  public Integer findIdByLoginId(String loginId){
    return mapper.findIdByLoginId(loginId);
  }

  public Integer findIdByEmail(String email){
    return mapper.findIdByEmail(email);
  }

  public void registerNewUserInfo(UserInfo userInfo){
    mapper.insertUserInfo(userInfo);
  }

  public void updateUserInfo(UserInfo userInfo){
    mapper.updateUserInfo(userInfo);
  }

  public void updatePassword(UserInfo userInfo){
    mapper.updatePassword(userInfo);
  }

  public void deleteUserInfoById(int id){
    mapper.deleteUserInfo(id);
  }
}
