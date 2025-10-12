package webapp.AwesomeCollect.provider;

import java.util.List;
import java.util.stream.Collectors;
import webapp.AwesomeCollect.provider.param.ExpiredUserParams;

public class UserProgressProvider {

  public String selectUserIdByUserIdList(ExpiredUserParams params){
    List<Integer> userIdList = params.userIdList();

    String inClause = userIdList.stream()
        .map(String::valueOf)
        .collect(Collectors.joining(","));

    return "SELECT user_id FROM user_progress "
        + "WHERE registered_date <= '"+ params.expiredDate() + "' "
        + "AND user_id IN (" + inClause + ")";
  }
}
