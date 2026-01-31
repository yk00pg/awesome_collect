package webapp.AwesomeCollect.common.constant;

/**
 * マッピングする値（URL)の定数クラス。
 */
public final class MappingValues {

  public static final String ROOT = "/";
  public static final String HEALTH = "/health";

  public static final String SIGNUP = "/signup";
  public static final String LOGIN = "/login";
  public static final String LOGIN_ERROR = LOGIN + "?error";
  public static final String GUEST_LOGIN = "/guest_login";
  public static final String LOGOUT = "/logout";
  public static final String TOP = "/top";

  private static final String EDIT = "/edit";

  public static final String MY_PAGE = "/mypage";
  public static final String MY_PAGE_EDIT = MY_PAGE + EDIT;
  public static final String MY_PAGE_CHANGE_PASSWORD = MY_PAGE + "/change_password";
  public static final String DELETE_ACCOUNT = MY_PAGE + "/delete_account";

  private static final String DATE = "/{date}";
  private static final String ID = "/{id}";

  public static final String TODO = "/todo";
  public static final String DAILY_TODO = TODO + DATE;
  public static final String DAILY_TODO_EDIT = TODO + EDIT + DATE;

  public static final String DONE = "/done";
  public static final String DAILY_DONE = DONE + DATE;
  public static final String DAILY_DONE_EDIT = DONE + EDIT + DATE;

  private static final String DETAIL = "/detail";

  public static final String GOAL = "/goal";
  public static final String GOAL_DETAIL = GOAL + DETAIL;
  public static final String GOAL_DETAIL_BY_ID = GOAL_DETAIL + ID;
  public static final String GOAL_EDIT_BY_ID = GOAL_DETAIL + EDIT + ID;

  public static final String MEMO = "/memo";
  public static final String MEMO_DETAIL = MEMO + DETAIL;
  public static final String MEMO_DETAIL_BY_ID = MEMO_DETAIL + ID;
  public static final String MEMO_EDIT_BY_ID = MEMO_DETAIL + EDIT + ID;

  public static final String ARTICLE_STOCK = "/article_stock";
  public static final String ARTICLE_STOCK_DETAIL = ARTICLE_STOCK + DETAIL;
  public static final String ARTICLE_STOCK_DETAIL_BY_ID = ARTICLE_STOCK_DETAIL + ID;
  public static final String ARTICLE_STOCK_EDIT_BY_ID = ARTICLE_STOCK_DETAIL + EDIT + ID;

  public static final String DASHBOARD = "/dashboard";
  public static final String DASHBOARD_LEARNING_TIME_CHART = DASHBOARD + "/chart/learning_time";
  public static final String DASHBOARD_ALL_TAG_TIME_CHART = DASHBOARD_LEARNING_TIME_CHART + "/all_tags";

  // エラーページ
  public static final String ERROR_NOT_ACCESSIBLE = "error/not-accessible";

  private MappingValues() {
    // インスタンス化を防止
  }
}
