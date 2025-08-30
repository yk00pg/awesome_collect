package webapp.AwesomeCollect.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import webapp.AwesomeCollect.entity.UserInfo;

/**
 * ユーザーのパスワード情報を扱うデータオブジェクト。
 */
@Data
public class UserPasswordDto {

  private static final String PASSWORD_PATTERN =
      "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[*_-])[A-Za-z0-9*_-]{8,32}$";

  @NotBlank
  @Pattern(regexp = PASSWORD_PATTERN, message = "{password.invalid}")
  private String password;

  @NotBlank
  @Pattern(regexp = PASSWORD_PATTERN, message = "{password.invalid}")
  private String confirmPassword;

  @Pattern(regexp = PASSWORD_PATTERN, message = "{password.invalid}")
  private String currentPassword;

  // 現在のパスワードが空欄か確認
  public boolean isBlankCurrentPassword(){
    return currentPassword == null;
  }

  // パスワードと確認用パスワードが不一致か確認
  public boolean isPasswordMismatch(){
    return !password.equals(confirmPassword);
  }

  // 入力されたパスワードをIDとともにエンティティに変換する
  public UserInfo toEntityWithIdAndPassword(
      int id, PasswordEncoder passwordEncoder){

    UserInfo userInfo = new UserInfo();
    userInfo.setId(id);
    userInfo.setPassword(passwordEncoder.encode(password));
    return userInfo;
  }
}
