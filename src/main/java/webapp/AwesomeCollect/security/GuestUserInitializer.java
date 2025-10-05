package webapp.AwesomeCollect.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import webapp.AwesomeCollect.entity.user.UserInfo;
import webapp.AwesomeCollect.repository.user.UserInfoRepository;
import webapp.AwesomeCollect.service.user.UserProgressService;

@Component
public class GuestUserInitializer implements CommandLineRunner {

  private final UserInfoRepository userInfoRepository;
  private final UserProgressService userProgressService;
  private final PasswordEncoder passwordEncoder;

  public GuestUserInitializer(
      UserInfoRepository userInfoRepository, UserProgressService userProgressService,
      PasswordEncoder passwordEncoder) {

    this.userInfoRepository = userInfoRepository;
    this.userProgressService = userProgressService;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) throws Exception {
    if(userInfoRepository.findUserInfoById(1)==null){
      UserInfo userInfo = new UserInfo();
      userInfo.setLoginId("Guest_User01");
      userInfo.setUserName("ゲストユーザー");
      userInfo.setEmail("guest_user01@mail.com");
      userInfo.setPassword(passwordEncoder.encode("GuestUser@123"));
      userInfoRepository.registerNewUserInfo(userInfo);
      userProgressService.createUserProgress(userInfo.getId());
    }
  }
}
