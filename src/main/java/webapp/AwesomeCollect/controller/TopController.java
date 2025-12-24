package webapp.AwesomeCollect.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import webapp.AwesomeCollect.common.constant.MappingValues;
import webapp.AwesomeCollect.common.constant.TemplateNames;

/**
 * トップページ（メインメニュー画面）のコントローラークラス。
 */
@Controller
public class TopController {

  // トップページ（メインメニュー画面）を表示する。
  @GetMapping(MappingValues.TOP)
  public String showTopPage(){
    return TemplateNames.TOP;
  }
}
