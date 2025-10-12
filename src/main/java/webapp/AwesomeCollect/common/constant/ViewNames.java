package webapp.AwesomeCollect.common.constant;

/**
 * View名（URLまたはhtmlファイル名）の定数クラス。
 */
public final class ViewNames {

  public static final String SIGNUP_PAGE = "/signup";
  public static final String LOGIN_PAGE = "/login";
  public static final String LOGIN_ERROR_PAGE = LOGIN_PAGE + "?error";
  public static final String GUEST_LOGIN = "/guest_login";
  public static final String LOGOUT_PAGE = "/logout";
  public static final String HOME_PAGE = "/home";

  private static final String EDIT_PAGE = "/edit";

  public static final String MY_PAGE = "/mypage";
  public static final String MY_PAGE_EDIT = MY_PAGE + EDIT_PAGE;
  public static final String MY_PAGE_CHANGE_PASSWORD = MY_PAGE + "/change_password";

  private static final String DATE = "/{date}";
  private static final String ID = "/{id}";

  public static final String TODO_PAGE = "/todo";
  public static final String TODO_EDIT_PAGE = TODO_PAGE + EDIT_PAGE;
  public static final String DAILY_TODO_VIEW_PAGE = TODO_PAGE + DATE;
  public static final String DAILY_TODO_EDIT_PAGE = TODO_EDIT_PAGE + DATE;

  public static final String DONE_PAGE = "/done";
  public static final String DONE_EDIT_PAGE = DONE_PAGE + EDIT_PAGE;
  public static final String DAILY_DONE_VIEW_PAGE = DONE_PAGE + DATE;
  public static final String DAILY_DONE_EDIT_PAGE = DONE_EDIT_PAGE + DATE;

  private static final String DETAIL_PAGE = "/detail";

  public static final String GOAL_PAGE = "/goal";
  public static final String GOAL_DETAIL_PAGE = GOAL_PAGE + DETAIL_PAGE;
  public static final String GOAL_DETAIL_BY_ID = GOAL_DETAIL_PAGE + ID;
  public static final String GOAL_EDIT_PAGE = GOAL_DETAIL_PAGE + EDIT_PAGE;
  public static final String GOAL_EDIT_BY_ID = GOAL_EDIT_PAGE + ID;

  public static final String MEMO_PAGE = "/memo";
  public static final String MEMO_DETAIL_PAGE = MEMO_PAGE + DETAIL_PAGE;
  public static final String MEMO_DETAIL_BY_ID = MEMO_DETAIL_PAGE + ID;
  public static final String MEMO_EDIT_PAGE = MEMO_DETAIL_PAGE + EDIT_PAGE;
  public static final String MEMO_EDIT_BY_ID = MEMO_EDIT_PAGE + ID;

  public static final String ARTICLE_STOCK_PAGE = "/article_stock";
  public static final String ARTICLE_STOCK_DETAIL_PAGE = ARTICLE_STOCK_PAGE + DETAIL_PAGE;
  public static final String ARTICLE_STOCK_DETAIL_BY_ID = ARTICLE_STOCK_DETAIL_PAGE + ID;
  public static final String ARTICLE_STOCK_EDIT_PAGE = ARTICLE_STOCK_DETAIL_PAGE + EDIT_PAGE;
  public static final String ARTICLE_STOCK_EDIT_BY_ID = ARTICLE_STOCK_EDIT_PAGE + ID;

  public static final String DASHBOARD_PAGE = "/dashboard";
  public static final String DASHBOARD_CHART_PAGE = DASHBOARD_PAGE + "/chart";
  public static final String DASHBOARD_LEARNING_TIME_CHART = DASHBOARD_CHART_PAGE + "/learning_time";
  public static final String DASHBOARD_ALL_TAG_TIME_CHART = DASHBOARD_LEARNING_TIME_CHART + "/all_tags";

  // エラーページ
  private static final String ERROR_PAGE = "/error";
  public static final String ERROR_NOT_ACCESSIBLE = ERROR_PAGE + "/not-accessible";

  private ViewNames() {
    // インスタンス下を防止
  }
}
