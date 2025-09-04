package webapp.AwesomeCollect.common.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RedirectUtil {

  // リダイレクト先を返す
  public static String redirectView(String viewNames, Object... pathVariables){
    String path = Arrays.stream(pathVariables)
        .map(Object::toString)
        .collect(Collectors.joining("/"));

    return "redirect:" + viewNames + (path.isEmpty() ? "" : "/" + path);
  }
}
