package com.awesomecollect.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/*
    NOTE:
    redirectViewは可変長引数だが、現在のアプリケーションでは単一パラメータでのみ使用。
    複数パラメータでの使用が発生したタイミングでテストケースを追加する。
 */
class RedirectUtilTest {

  @Test
  void redirectView_リダイレクト先を文字列結合して返す() {
    String viewNames = "/login";
    String result = RedirectUtil.redirectView(viewNames);

    assertEquals(
        "redirect:/login",
        result,
        "パラメータなしでリダイレクト先のパスが返る");
  }

  @Test
  void redirectView_リダイレクト先を日付パラメータ付きで文字列結合して返す() {
    String viewNames = "/done";
    LocalDate date = LocalDate.of(2025, 12, 24);
    String result = RedirectUtil.redirectView(viewNames, date);

    assertEquals(
        "redirect:/done/2025-12-24",
        result,
        "日付パラメータ付きでリダイレクト先のパスが返る");
  }

  @Test
  void redirectView_リダイレクト先をIDパラメータ付きで文字列結合して返す() {
    String viewNames = "/goal/detail";
    int id = 10;
    String result = RedirectUtil.redirectView(viewNames, id);

    assertEquals(
        "redirect:/goal/detail/10",
        result,
        "IDパラメータ付きでリダイレクト先のパスが返る");
  }
}