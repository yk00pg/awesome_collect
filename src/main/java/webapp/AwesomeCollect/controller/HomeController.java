package webapp.AwesomeCollect.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ホーム画面のコントローラークラス。
 */
@Controller
public class HomeController {

  // ホーム画面を表示
  @GetMapping(value = "/home")
  public String showHomeView(){
    return "/home";
  }
}
