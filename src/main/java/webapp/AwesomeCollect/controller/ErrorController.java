package webapp.AwesomeCollect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import webapp.AwesomeCollect.common.constant.MappingValues;
import webapp.AwesomeCollect.common.constant.TemplateNames;

/**
 * エラーページのコントローラークラス。
 */
@Controller
public class ErrorController {

  // アクセス不可時のエラーページを表示する。
  @GetMapping(MappingValues.ERROR_NOT_ACCESSIBLE)
  public String showNotAccessibleError() {
    return TemplateNames.ERROR_NOT_ACCESSIBLE;
  }
}
