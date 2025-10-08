package webapp.AwesomeCollect.service.user;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.entity.user.UserInfo;
import webapp.AwesomeCollect.provider.param.ExpiredUserParams;
import webapp.AwesomeCollect.repository.user.UserInfoRepository;
import webapp.AwesomeCollect.repository.user.UserProgressRepository;

@Service
public class GuestUserService {

  private final UserInfoRepository userInfoRepository;
  private final UserProgressService userProgressService;
  private final UserProgressRepository userProgressRepository;
  private final PasswordEncoder passwordEncoder;
  private final DeleteUserDataService deleteUserDataService;

  private static final Logger logger = LoggerFactory.getLogger(GuestUserService.class);

  public GuestUserService(
      UserInfoRepository userInfoRepository, UserProgressService userProgressService,
      UserProgressRepository userProgressRepository, PasswordEncoder passwordEncoder,
      DeleteUserDataService deleteUserDataService) {

    this.userInfoRepository = userInfoRepository;
    this.userProgressService = userProgressService;
    this.userProgressRepository = userProgressRepository;
    this.passwordEncoder = passwordEncoder;
    this.deleteUserDataService = deleteUserDataService;
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

  @Transactional
  public void cleanupGuestUsers(){
    List<Integer> guestUserIdList = userInfoRepository.selectGuestUserId();
    if(guestUserIdList==null || guestUserIdList.isEmpty()){
      logger.info("No expired guest users found. Cleanup skipped.");
      return;
    }

    logger.info("=== Guest User Cleanup Started. Target count: {} ===", guestUserIdList.size());

    List<Integer> expiredGuestUserIdList =
        userProgressRepository.searchExpiredUserIdByUserId(
            new ExpiredUserParams(guestUserIdList, LocalDate.now().minusDays(1)));

    for(int expiredGuestUserId: expiredGuestUserIdList) {
      deleteUserDataService.deleteUserData(expiredGuestUserId);
      logger.info("Deleted guest user data: userId={}", expiredGuestUserId);
    }

    logger.info("=== Guest User Cleanup Finished. Total deleted: {} ===", expiredGuestUserIdList.size());
  }
}
