package webapp.AwesomeCollect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import webapp.AwesomeCollect.common.constant.ViewNames;

/**
 * エラーページのコントローラークラス。
 */
@Controller
public class ErrorController {

  // アクセス不可時のエラーページを表示する。
  @GetMapping(ViewNames.ERROR_NOT_ACCESSIBLE)
  public String showNotAccessibleView() {
    return ViewNames.ERROR_NOT_ACCESSIBLE;
  }
}
