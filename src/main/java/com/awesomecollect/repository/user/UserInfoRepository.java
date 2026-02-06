package com.awesomecollect.repository.user;

import com.awesomecollect.entity.user.UserInfo;
import com.awesomecollect.mapper.user.UserInfoMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ユーザー情報のリポジトリクラス。
 */
@Repository
public class UserInfoRepository {

  private final UserInfoMapper mapper;

  public UserInfoRepository(UserInfoMapper mapper) {
    this.mapper = mapper;
  }

  public List<Integer> selectGuestUserId(){
    return mapper.searchGuestUserId();
  }

  public Optional<UserInfo> findUserInfoByBinaryLoginId(String loginId){
    return Optional.ofNullable(mapper.findUserInfoByBinaryLoginId(loginId));
  }

  public UserInfo findUserInfoById(int id){
    return mapper.findUserInfoById(id);
  }

  public boolean existsUserInfoByLoginId(String loginId){
    return mapper.existsUserInfoByLoginId(loginId);
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
