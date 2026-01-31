package webapp.AwesomeCollect.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import webapp.AwesomeCollect.service.user.GuestUserService;

/**
 * 期限切れゲストユーザーアカウントの定期削除をスケジューリングするクラス。
 */
@Component
@RequiredArgsConstructor
public class GuestUserCleanupScheduler {

  private final GuestUserService guestUserService;

  @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Tokyo")
  public void cleanupGuestUsers() {
    guestUserService.cleanupExpiredGuestUsers();
  }
}
