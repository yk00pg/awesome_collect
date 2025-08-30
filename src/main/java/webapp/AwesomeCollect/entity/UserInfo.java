package webapp.AwesomeCollect.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ユーザー情報を扱うエンティティクラス。DBに存在するテーブルと連動する。
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
