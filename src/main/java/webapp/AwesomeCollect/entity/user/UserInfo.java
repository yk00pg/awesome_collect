package webapp.AwesomeCollect.entity.user;

import lombok.Builder;
import lombok.Getter;

/**
 * ユーザー情報を扱うオブジェクト。DBに存在するテーブルと連動する。
 */
@Getter
@Builder
public class UserInfo {

  private int id;
  private String loginId;
  private String userName;
  private String email;
  private String password;
  private boolean isGuest;

}
