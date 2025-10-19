package webapp.AwesomeCollect.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import webapp.AwesomeCollect.entity.user.UserInfo;

/**
 * ユーザー基本情報データオブジェクト。
 */
@Data
public class UserBasicInfoDto {

  private static final String LOGIN_ID_PATTERN =
      "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9_-]{6,20}$";
  private static final int NAME_MAX_SIZE = 20;
  private static final int EMAIL_MAX_SIZE = 254;

  @NotBlank(message = "{loginId.blank}")
  @Pattern(regexp = LOGIN_ID_PATTERN, message = "{loginId.invalid}")
  private String loginId;

  @Size(max = NAME_MAX_SIZE, message = "{userName.invalid}")
  private String userName;

  @Email(message = "{email.invalid.pattern}")
  @Size(max = EMAIL_MAX_SIZE, message = "{email.invalid.size}")
  private String email;

  /**
   * 入力されたデータを更新用のエンティティに変換する。
   *
   * @param id ユーザーID
   * @return 更新用のエンティティ
   */
  public UserInfo toEntityForUpdate(int id) {
    return UserInfo.builder()
        .id(id)
        .loginId(loginId)
        .userName(userName)
        .email(email.isBlank() ? null : email)
        .build();
  }

  /**
   * DBから取得したユーザー情報を基本情報データオブジェクトに変換する。
   *
   * @param userInfo ユーザー情報
   * @return データオブジェクト
   */
  public static UserBasicInfoDto fromEntity(UserInfo userInfo) {
    UserBasicInfoDto dto = new UserBasicInfoDto();
    dto.loginId = userInfo.getLoginId();
    dto.userName = userInfo.getUserName();
    dto.email = userInfo.getEmail();
    return dto;
  }
}
