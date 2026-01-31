package com.awesomecollect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.awesomecollect.common.constant.MappingValues;
import com.awesomecollect.common.util.RedirectUtil;

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
