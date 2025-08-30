package webapp.AwesomeCollect.dto.user;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import webapp.AwesomeCollect.entity.UserInfo;

/**
 * ユーザー情報の統合データオブジェクト。基本情報とパスワードのデータオブジェクトを内包する。
 */
@Data
public class UserInfoDto {

  @Valid
  private UserBasicInfoDto basicInfoDto;
  @Valid
  private UserPasswordDto passwordDto;

  // 入力されたユーザー情報をエンティティに変換する
  public UserInfo toEntity(PasswordEncoder passwordEncoder){
    UserInfo userInfo = new UserInfo();
    userInfo.setLoginId(basicInfoDto.getLoginId());
    userInfo.setUserName(basicInfoDto.getUserName());
    userInfo.setEmail(basicInfoDto.getEmail());
    userInfo.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
    return userInfo;
  }
}
