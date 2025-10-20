package webapp.AwesomeCollect.provider;

import java.util.List;
import java.util.stream.Collectors;
import webapp.AwesomeCollect.provider.param.ExpiredGuestUserParams;

/**
 * ユーザー進捗状況のプロバイダクラス。
 */
public class UserProgressProvider {

  // Mapperに渡された引数オブジェクトから取得したリストを文字列結合してSQL文を作成する。
  public String selectUserIdByUserIdList(ExpiredGuestUserParams params){
    List<Integer> userIdList = params.guestUserIdList();

    String inClause = userIdList.stream()
        .map(String::valueOf)
        .collect(Collectors.joining(","));

    return "SELECT user_id FROM user_progress "
        + "WHERE registered_date <= '"+ params.expiredDate() + "' "
        + "AND user_id IN (" + inClause + ")";
  }
}
