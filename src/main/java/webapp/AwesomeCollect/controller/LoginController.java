package webapp.AwesomeCollect.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.constant.ViewNames;
import webapp.AwesomeCollect.common.util.MessageUtil;
import webapp.AwesomeCollect.common.util.RedirectUtil;
import webapp.AwesomeCollect.entity.user.UserInfo;
import webapp.AwesomeCollect.service.user.GuestUserService;

/**
 * ログインページのコントローラークラス。
 */
@Controller
public class LoginController {

  private final GuestUserService guestUserService;
  private final AuthenticationManager authenticationManager;
  private final MessageUtil messageUtil;

  public LoginController(
      GuestUserService guestUserService, AuthenticationManager authenticationManager,
      MessageUtil messageUtil){

    this.guestUserService = guestUserService;
    this.authenticationManager = authenticationManager;
    this.messageUtil = messageUtil;
  }

  // ログインフォームを表示する。
  @GetMapping(ViewNames.LOGIN_PAGE)
  public String showLoginForm() {
    return ViewNames.LOGIN_PAGE;
  }

  // ログイン失敗時にエラーメッセージとヒントを表示する。
  @GetMapping(value = ViewNames.LOGIN_PAGE, params = "error")
  public String showLoginFailureMessage(Model model){
    model.addAttribute(
        AttributeNames.FAILURE_MESSAGE,
        messageUtil.getMessage(MessageKeys.LOGIN_FAILURE));
    model.addAttribute(
        AttributeNames.FAILURE_HINT,
        messageUtil.getMessage(MessageKeys.LOGIN_FAILURE_HINT));

    return ViewNames.LOGIN_PAGE;
  }

  // ゲストユーザーとしてログインする。
  @PostMapping("/guest_login")
  public String guestLogin(HttpServletRequest request){

    UserInfo guestUser = guestUserService.createGuestUser();

    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(guestUser.getLoginId(), "GuestUser@123");

    Authentication auth = authenticationManager.authenticate(authToken);
    SecurityContextHolder.getContext().setAuthentication(auth);

    // SecurityContextをセッションに保存
    request.getSession(true).setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        SecurityContextHolder.getContext());

    return RedirectUtil.redirectView(ViewNames.TOP_PAGE);
  }
}
