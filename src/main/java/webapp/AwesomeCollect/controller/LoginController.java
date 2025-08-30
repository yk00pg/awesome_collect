package webapp.AwesomeCollect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import webapp.AwesomeCollect.common.util.MessageUtil;

/**
 * ログインページのコントローラークラス。
 */
@Controller
public class LoginController {

  private final MessageUtil messageUtil;

  public LoginController(MessageUtil messageUtil){
    this.messageUtil = messageUtil;
  }

  // ログインフォームを表示
  @GetMapping("/login")
  public String showLoginForm() {
    return "/login";
  }

  // ログイン失敗時にエラーメッセージとヒントを表示
  @GetMapping(value = "/login", params = "error")
  public String showLoginFailureMessage(Model model){
    model.addAttribute(
        "failureMessage", messageUtil.getMessage("login.failure"));
    model.addAttribute(
        "failureHint", messageUtil.getMessage("login.failure.hint"));
    return "/login";
  }
}
