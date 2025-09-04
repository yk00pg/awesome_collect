package webapp.AwesomeCollect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import webapp.AwesomeCollect.common.constant.AttributeNames;
import webapp.AwesomeCollect.common.constant.MessageKeys;
import webapp.AwesomeCollect.common.constant.ViewNames;
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
  @GetMapping(ViewNames.LOGIN_PAGE)
  public String showLoginForm() {
    return ViewNames.LOGIN_PAGE;
  }

  // ログイン失敗時にエラーメッセージとヒントを表示
  @GetMapping(value = ViewNames.LOGIN_PAGE, params = "error")
  public String showLoginFailureMessage(Model model){
    model.addAttribute(
        AttributeNames.FAILURE_MESSAGE, messageUtil.getMessage(MessageKeys.LOGIN_FAILURE));
    model.addAttribute(
        AttributeNames.FAILURE_HINT, messageUtil.getMessage(MessageKeys.LOGIN_FAILURE_HINT));
    return ViewNames.LOGIN_PAGE;
  }
}
