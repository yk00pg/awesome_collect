package webapp.AwesomeCollect.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.AwesomeCollect.common.constant.ViewNames;
import webapp.AwesomeCollect.service.user.DeleteUserDataService;

/**
 * ゲストユーザーがログアウトする際にデータを削除するクラス。
 */
@Component
@RequiredArgsConstructor
public class GuestLogoutSuccessHandler implements LogoutSuccessHandler {

  private final DeleteUserDataService deleteUserDataService;

  @Transactional
  @Override
  public void onLogoutSuccess(
      HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {

    if(authentication != null) {
      CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

      if(user.isGuest()){
        deleteUserDataService.deleteUserData(user.getId());
      }
    }

    response.sendRedirect(ViewNames.LOGIN_PAGE);
  }
}
