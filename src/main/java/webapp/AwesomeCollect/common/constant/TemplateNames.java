package webapp.AwesomeCollect.common.constant;

/**
 * Thymeleafテンプレート名の定数クラス。
 */
public final class TemplateNames {

  public static final String SIGNUP = "signup";
  public static final String LOGIN = "login";
  public static final String TOP = "top";

  private static final String EDIT = "/edit";

  public static final String MY_PAGE = "mypage";
  public static final String MY_PAGE_EDIT = MY_PAGE + EDIT;
  public static final String MY_PAGE_CHANGE_PASSWORD = MY_PAGE + "/change_password";

  public static final String TODO = "todo";
  public static final String TODO_EDIT = TODO + EDIT;

  public static final String DONE = "done";
  public static final String DONE_EDIT = DONE + EDIT;

  private static final String DETAIL = "/detail";

  public static final String GOAL = "goal";
  public static final String GOAL_DETAIL = GOAL + DETAIL;
  public static final String GOAL_EDIT = GOAL_DETAIL + EDIT;

  public static final String MEMO = "memo";
  public static final String MEMO_DETAIL = MEMO + DETAIL;
  public static final String MEMO_EDIT = MEMO_DETAIL + EDIT;

  public static final String ARTICLE_STOCK = "article_stock";
  public static final String ARTICLE_STOCK_DETAIL = ARTICLE_STOCK + DETAIL;
  public static final String ARTICLE_STOCK_EDIT = ARTICLE_STOCK_DETAIL + EDIT;

  public static final String DASHBOARD = "dashboard";
  public static final String DASHBOARD_LEARNING_TIME_CHART = DASHBOARD + "/chart/learning_time";
  public static final String DASHBOARD_ALL_TAG_TIME_CHART = DASHBOARD_LEARNING_TIME_CHART + "/all_tags";

  // エラーページ
  public static final String ERROR_NOT_ACCESSIBLE = "error/not-accessible";

  private TemplateNames() {
    // インスタンス下を防止
  }
}
