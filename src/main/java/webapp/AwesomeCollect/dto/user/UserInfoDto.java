package webapp.AwesomeCollect.dto.user;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import webapp.AwesomeCollect.entity.user.UserInfo;

/**
 * ユーザー情報統合データオブジェクト。基本情報とパスワードのデータオブジェクトを内包する。
 */
@Data
public class UserInfoDto {

  @Valid
  private UserBasicInfoDto basicInfoDto;
  @Valid
  private UserPasswordDto passwordDto;

  /**
   * 入力されたデータをエンティティに変換する。
   *
   * @param passwordEncoder パスワードエンコーダー
   * @return 新規登録用のエンティティ
   */
  public UserInfo toEntityForSignup(PasswordEncoder passwordEncoder) {
    UserInfo userInfo = new UserInfo();
    userInfo.setLoginId(basicInfoDto.getLoginId());
    userInfo.setUserName(basicInfoDto.getUserName());
    userInfo.setEmail(basicInfoDto.getEmail().isBlank() ? null : basicInfoDto.getEmail());
    userInfo.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
    return userInfo;
  }
}
