package com.awesomecollect.common.util;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * リダイレクト先のパス名を生成するクラス。
 */
public final class RedirectUtil {

  /**
   * リダイレクト先を文字列結合する。
   *
   * @param viewNames View名
   * @param pathVariables パスパラメータ
   * @return  リダイレクト先
   */
  public static String redirectView(String viewNames, Object... pathVariables){
    String path = Arrays.stream(pathVariables)
        .map(Object::toString)
        .collect(Collectors.joining("/"));

    return "redirect:" + viewNames + (path.isEmpty() ? "" : "/" + path);
  }
}
