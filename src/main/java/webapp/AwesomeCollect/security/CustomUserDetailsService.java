package webapp.AwesomeCollect.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.entity.UserInfo;
import webapp.AwesomeCollect.repository.UserInfoRepository;

/**
 * ログイン認証に係るサービスクラス。
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserInfoRepository userInfoRepository;

  public CustomUserDetailsService(UserInfoRepository userInfoRepository){
    this.userInfoRepository = userInfoRepository;
  }

  /**
   * ログインIDを基にDBからユーザー情報を取得し、nullでなければカスタムユーザー情報として返す。
   *
   * @param loginId ログインID
   * @return  カスタムユーザー情報
   * @throws UsernameNotFoundException ユーザーが見つからなかった場合
   */
  @Override
  public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
    UserInfo userInfo = userInfoRepository.findUserInfoByBinaryLoginId(loginId);
    if(userInfo == null){
      throw new UsernameNotFoundException("failure.loginID");
    }

    return new CustomUserDetails(userInfo);
  }
}
