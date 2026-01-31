package com.awesomecollect.dto.user;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.awesomecollect.entity.user.UserInfo;

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
    return UserInfo.builder()
        .loginId(basicInfoDto.getLoginId())
        .userName(basicInfoDto.getUserName())
        .email(basicInfoDto.getEmail().isBlank() ? null : basicInfoDto.getEmail())
        .password(passwordEncoder.encode(passwordDto.getPassword()))
        .isGuest(false)
        .build();
  }
}
