package com.awesomecollect.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.awesomecollect.common.constant.MappingValues;
import com.awesomecollect.common.constant.TemplateNames;

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
