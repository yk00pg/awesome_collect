package com.awesomecollect.mapper.user;

import com.awesomecollect.entity.user.UserInfo;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserInfoMapper {

  @Select("""
      SELECT id FROM user_info
      WHERE isGuest=true
      """)
  List<Integer> searchGuestUserId();

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
      SELECT EXISTS(
        SELECT 1 FROM user_info
        WHERE login_id=#{loginId}
      """)
  boolean existsUserInfoByLoginId(String loginId);

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
      INSERT user_info(login_id, user_name, email, password, isGuest)
      VALUES(#{loginId}, #{userName}, #{email}, #{password}, #{isGuest})
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

  @Delete("""
      DELETE FROM user_info
      WHERE id=#{id}
      """)
  void deleteUserInfo(int id);
}
