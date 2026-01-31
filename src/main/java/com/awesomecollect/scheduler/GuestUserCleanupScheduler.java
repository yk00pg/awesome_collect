package com.awesomecollect.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.awesomecollect.service.user.GuestUserService;

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
