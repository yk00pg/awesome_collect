package webapp.AwesomeCollect.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import webapp.AwesomeCollect.common.constant.ViewNames;

/**
 * ホーム画面のコントローラークラス。
 */
@Controller
public class HomeController {

  // ホーム画面を表示
  @GetMapping(ViewNames.HOME_PAGE)
  public String showHomeView(){
    return ViewNames.HOME_PAGE;
  }
}
