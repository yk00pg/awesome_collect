package com.awesomecollect.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.awesomecollect.entity.user.UserInfo;

/**
 * ユーザーパスワード情報データオブジェクト。
 */
@Data
public class UserPasswordDto {

  private static final String PASSWORD_PATTERN =
      "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[*_-])[A-Za-z0-9*_-]{8,32}$";
  private static final String PASSWORD_INVALID = "{password.invalid}";

  @NotBlank
  @Pattern(regexp = PASSWORD_PATTERN, message = PASSWORD_INVALID)
  private String password;

  @NotBlank
  @Pattern(regexp = PASSWORD_PATTERN, message = PASSWORD_INVALID)
  private String confirmPassword;

  @Pattern(regexp = PASSWORD_PATTERN, message = PASSWORD_INVALID)
  private String currentPassword;

  /**
   * 入力されたデータを更新用のエンティティに変換する。
   *
   * @param id  ユーザーID
   * @param passwordEncoder パスワードエンコーダー
   * @return  更新用のエンティティ
   */
  public UserInfo toEntityForUpdate(int id, PasswordEncoder passwordEncoder){
    return UserInfo.builder()
        .id(id)
        .password(passwordEncoder.encode(password))
        .build();
  }
}
