package webapp.AwesomeCollect.service.user;

import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import webapp.AwesomeCollect.entity.user.UserInfo;
import webapp.AwesomeCollect.repository.user.UserInfoRepository;

@Service
public class GuestUserService {

  private final UserInfoRepository userInfoRepository;
  private final UserProgressService userProgressService;
  private final PasswordEncoder passwordEncoder;

  public GuestUserService(
      UserInfoRepository userInfoRepository, UserProgressService userProgressService,
      PasswordEncoder passwordEncoder) {

    this.userInfoRepository = userInfoRepository;
    this.userProgressService = userProgressService;
    this.passwordEncoder = passwordEncoder;
  }

  public UserInfo createGuestUser() {
    String randomId = UUID.randomUUID().toString().substring(0, 8);
    String loginId = "guest_" + randomId;

    while(userInfoRepository.findUserInfoByLoginId(loginId) != null){
      randomId = UUID.randomUUID().toString().substring(0, 8);
      loginId = "guest_" + randomId;
    }

    UserInfo guestUser = UserInfo.builder()
        .loginId(loginId)
        .userName("ゲストユーザー")
        .email(loginId + "@mail.com")
        .password(passwordEncoder.encode("GuestUser@123"))
        .isGuest(true)
        .build();

    userInfoRepository.registerNewUserInfo(guestUser);
    userProgressService.createUserProgress(guestUser.getId());

    return guestUser;
  }
}
