package webapp.AwesomeCollect.provider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * タグのプロバイダクラス。
 */
public class TagProvider {

  // Mapperに渡されるリストを文字列結合して返す
  public String selectIdsByTagIdList(Map<String, Object> params) {
    @SuppressWarnings("unchecked")
    List<Integer> tagIdList = (List<Integer>) params.get("tagIdList");

    String inClause = tagIdList.stream()
        .map(String::valueOf)
        .collect(Collectors.joining(","));

    return "SELECT name FROM tag WHERE id IN (" + inClause + ")";
  }
}
