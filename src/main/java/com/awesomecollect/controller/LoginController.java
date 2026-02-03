package com.awesomecollect.controller;

import com.awesomecollect.common.constant.AttributeNames;
import com.awesomecollect.common.constant.GuestUser;
import com.awesomecollect.common.constant.MappingValues;
import com.awesomecollect.common.constant.MessageKeys;
import com.awesomecollect.common.constant.TemplateNames;
import com.awesomecollect.common.util.MessageUtil;
import com.awesomecollect.common.util.RedirectUtil;
import com.awesomecollect.controller.web.SessionManager;
import com.awesomecollect.entity.user.UserInfo;
import com.awesomecollect.security.CustomUserDetails;
import com.awesomecollect.service.user.GuestUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * ログインページのコントローラークラス。
 */
@Controller
@RequiredArgsConstructor
public class LoginController {

  private final GuestUserService guestUserService;
  private final AuthenticationManager authenticationManager;
  private final MessageUtil messageUtil;
  private final SessionManager sessionManager;

  // ログイン済みの場合はトップページに遷移し、そうでない場合はログインページを表示する。
  @GetMapping(MappingValues.LOGIN)
  public String showLoginPage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
    if(customUserDetails != null){
      return RedirectUtil.redirectView(MappingValues.TOP);
    }

    return TemplateNames.LOGIN;
  }

  // ログイン失敗時にエラーメッセージとヒントを表示する。
  @GetMapping(value = MappingValues.LOGIN, params = "error")
  public String showLoginFailureMessage(Model model){
    model.addAttribute(
        AttributeNames.FAILURE_MESSAGE,
        messageUtil.getMessage(MessageKeys.LOGIN_FAILURE));
    model.addAttribute(
        AttributeNames.FAILURE_HINT,
        messageUtil.getMessage(MessageKeys.LOGIN_FAILURE_HINT));

    return TemplateNames.LOGIN;
  }

  // ゲストユーザーとしてログインする。
  @PostMapping(MappingValues.GUEST_LOGIN)
  public String guestLogin(HttpServletRequest request){

    UserInfo guestUser = guestUserService.createGuestUser();
    sessionManager.disableCachedAwesomePoints();
    sessionManager.disableCachedLearningDays();
    sessionManager.disableCachedLearningTime();

    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(guestUser.getLoginId(), GuestUser.PASSWORD);

    Authentication auth = authenticationManager.authenticate(authToken);
    SecurityContextHolder.getContext().setAuthentication(auth);

    // SecurityContextをセッションに保存
    request.getSession(true).setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        SecurityContextHolder.getContext());

    return RedirectUtil.redirectView(MappingValues.TOP);
  }
}
