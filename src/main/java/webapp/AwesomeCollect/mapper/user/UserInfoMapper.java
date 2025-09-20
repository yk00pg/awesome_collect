package webapp.AwesomeCollect.mapper.user;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import webapp.AwesomeCollect.entity.user.UserInfo;

@Mapper
public interface UserInfoMapper {

  @Select("""
      SELECT * FROM user_info
      WHERE BINARY login_id=#{loginId}
      """)
  UserInfo findUserInfoByBinaryLoginId(String loginId);

  @Select("""
      SELECT * FROM user_info
      WHERE id=#{id}
      """)
  UserInfo findUserInfoById(int id);

  @Select("""
      SELECT id FROM user_info
      WHERE login_id=#{loginId}
      """)
  Integer findIdByLoginId(String userId);

  @Select("""
      SELECT id FROM user_info
      WHERE email=#{email}
      """)
  Integer findIdByEmail(String email);

  // @OptionsをつけてDBで自動採番されるIDを取得してエンティティに付与
  @Insert("""
      INSERT user_info(login_id, user_name, email, password)
      VALUES(#{loginId}, #{userName}, #{email}, #{password})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertUserInfo(UserInfo userInfo);

  @Update("""
      UPDATE user_info
      SET login_id=#{loginId}, user_name=#{userName}, email=#{email}
      WHERE id=#{id}
      """)
  void updateUserInfo(UserInfo userInfo);

  @Update("""
      UPDATE user_info
      SET password=#{password}
      WHERE id=#{id}
      """)
  void updatePassword(UserInfo userInfo);
}
