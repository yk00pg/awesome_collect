package webapp.AwesomeCollect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import webapp.AwesomeCollect.common.constant.MappingValues;
import webapp.AwesomeCollect.common.util.RedirectUtil;

/**
 * ルートパスのコントローラークラス。
 */
@Controller
public class RootController {

  @GetMapping(MappingValues.ROOT)
  public String redirectRoot(){
    return RedirectUtil.redirectView(MappingValues.LOGIN);
  }
}
