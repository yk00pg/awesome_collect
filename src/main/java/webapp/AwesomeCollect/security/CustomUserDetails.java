package webapp.AwesomeCollect.security;

import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import webapp.AwesomeCollect.entity.UserInfo;

/**
 * カスタムユーザー情報を扱うオブジェクト。ログインID、パスワードなどの情報を持つ。
 */
public record CustomUserDetails(UserInfo userInfo) implements UserDetails {

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getPassword() {
    return userInfo.getPassword();
  }

  @Override
  public String getUsername() {
    return userInfo.getLoginId();
  }

  public int getId() {
    return userInfo.getId();
  }

  public String getEmail() {
    return userInfo.getEmail();
  }
}
