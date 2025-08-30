package webapp.AwesomeCollect.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import webapp.AwesomeCollect.entity.UserInfo;

/**
 * ユーザーの基本情報を扱うデータオブジェクト。
 */
@Data
public class UserBasicInfoDto {

  private static final String LOGIN_ID_PATTERN =
      "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9_-]{6,20}$";

  @NotBlank(message = "{loginId.blank}")
  @Pattern(regexp = LOGIN_ID_PATTERN, message = "{loginId.invalid}")
  private String loginId;

  @Size(max = 20, message = "{userName.invalid}")
  private String userName;

  @NotBlank(message = "{email.blank}")
  @Email(message = "{email.invalid.pattern}")
  @Size(max = 254, message = "{email.invalid.size}")
  private String email;

  // 入力されたユーザーの基本情報とIDをエンティティに変換する
  public UserInfo toEntityWithId(int id){
    UserInfo userInfo = new UserInfo();
    userInfo.setId(id);
    userInfo.setLoginId(loginId);
    userInfo.setUserName(userName);
    userInfo.setEmail(email);
    return userInfo;
  }

  // DBから取得したユーザーの基本情報をデータオブジェクトに変換する
  public static UserBasicInfoDto fromEntity(UserInfo userInfo){
    UserBasicInfoDto dto = new UserBasicInfoDto();
    dto.loginId = userInfo.getLoginId();
    dto.userName = userInfo.getUserName();
    dto.email = userInfo.getEmail();
    return dto;
  }
}
