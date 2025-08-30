package webapp.AwesomeCollect.security;

import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import webapp.AwesomeCollect.entity.UserInfo;

/**
 * カスタムユーザー情報を扱うオブジェクト。ログインID、パスワードなどの情報を持つ。
 */
public class CustomUserDetails  implements UserDetails {

  private final UserInfo userInfo;

  public CustomUserDetails(UserInfo userInfo){
    this.userInfo = userInfo;
  }

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

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public int getId(){
    return userInfo.getId();
  }

  public String getEmail(){
    return userInfo.getEmail();
  }
}
