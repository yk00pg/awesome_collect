package webapp.AwesomeCollect.entity.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ユーザー情報を扱うオブジェクト。DBに存在するテーブルと連動する。
 */
@Getter
@Setter
@NoArgsConstructor
public class UserInfo {

  private int id;
  private String loginId;
  private String userName;
  private String email;
  private String password;
}
