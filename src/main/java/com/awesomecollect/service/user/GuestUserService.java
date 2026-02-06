package com.awesomecollect.service.user;

import com.awesomecollect.common.constant.GuestUser;
import com.awesomecollect.entity.user.UserInfo;
import com.awesomecollect.provider.param.ExpiredGuestUserParams;
import com.awesomecollect.repository.user.UserInfoRepository;
import com.awesomecollect.repository.user.UserProgressRepository;
import com.awesomecollect.service.DummyDataService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ゲストユーザーのサービスクラス。
 */
@Service
@RequiredArgsConstructor
public class GuestUserService {

  private final UserInfoRepository userInfoRepository;
  private final UserProgressService userProgressService;
  private final UserProgressRepository userProgressRepository;
  private final PasswordEncoder passwordEncoder;
  private final DummyDataService dummyDataService;
  private final DeleteUserDataService deleteUserDataService;

  private static final Logger logger = LoggerFactory.getLogger(GuestUserService.class);

  /**
   * 新規ゲストユーザーアカウントとユーザー進捗状況を作成し、ダミーデータを登録する。
   *
   * @return  ユーザー情報
   */
  @Transactional
  public UserInfo createGuestUser() {
    String randomId = UUID.randomUUID().toString().substring(0, 8);
    String loginId = GuestUser.LOGIN_ID + randomId;

    while(userInfoRepository.existsUserInfoByLoginId(loginId)){
      randomId = UUID.randomUUID().toString().substring(0, 8);
      loginId = GuestUser.LOGIN_ID + randomId;
    }

    UserInfo guestUser = UserInfo.builder()
        .loginId(loginId)
        .userName(GuestUser.NAME)
        .email(loginId + GuestUser.EMAIL)
        .password(passwordEncoder.encode(GuestUser.PASSWORD))
        .isGuest(true)
        .build();

    userInfoRepository.registerNewUserInfo(guestUser);
    int guestUserId = guestUser.getId();
    userProgressService.createUserProgress(guestUserId);

    dummyDataService.registerDummyData(guestUserId);

    return guestUser;
  }

  /**
   * 期限切れのゲストユーザーアカウントを削除し、ログを出力する。
   */
  @Transactional
  public void cleanupExpiredGuestUsers(){
    List<Integer> guestUserIdList = userInfoRepository.selectGuestUserId();
    if(guestUserIdList==null || guestUserIdList.isEmpty()){
      logger.info("No expired guest users found. Cleanup skipped.");
      return;
    }

    logger.info("=== Guest User Cleanup Started. Target count: {} ===", guestUserIdList.size());

    List<Integer> expiredGuestUserIdList =
        userProgressRepository.searchExpiredUserIdByUserId(
            new ExpiredGuestUserParams(guestUserIdList, LocalDate.now().minusDays(1)));

    for(int expiredGuestUserId: expiredGuestUserIdList) {
      deleteUserDataService.deleteUserData(expiredGuestUserId);
      logger.info("Deleted guest user data: userId={}", expiredGuestUserId);
    }

    logger.info("=== Guest User Cleanup Finished. Total deleted: {} ===", expiredGuestUserIdList.size());
  }
}
