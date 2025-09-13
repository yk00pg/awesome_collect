package webapp.AwesomeCollect.common.constant;

public class ViewNames {

  public static final String SIGNUP_PAGE = "/signup";
  public static final String LOGIN_PAGE = "/login";
  public static final String HOME_PAGE = "/home";

  public static final String MY_PAGE = "/mypage";
  public static final String MY_PAGE_EDIT = MY_PAGE + "/edit";
  public static final String MY_PAGE_CHANGE_PASSWORD = MY_PAGE + "/change_password";

  private static final String DATE = "/{date}";
  private static final String ID = "/{id}";

  public static final String TODO_PAGE = "/todo";
  public static final String TODO_EDIT_PAGE = TODO_PAGE + "/edit";
  public static final String DAILY_TODO_VIEW_PAGE = TODO_PAGE + DATE;
  public static final String DAILY_TODO_EDIT_PAGE = TODO_EDIT_PAGE + DATE;

  public static final String DONE_PAGE = "/done";
  public static final String DONE_EDIT_PAGE = DONE_PAGE + "/edit";
  public static final String DAILY_DONE_VIEW_PAGE = DONE_PAGE + DATE;
  public static final String DAILY_DONE_EDIT_PAGE = DONE_EDIT_PAGE + DATE;

  public static final String GOAL_PAGE = "/goal";
  public static final String GOAL_DETAIL_PAGE = GOAL_PAGE + "/detail";
  public static final String GOAL_DETAIL_BY_ID = GOAL_DETAIL_PAGE + ID;
  public static final String GOAL_EDIT_PAGE = GOAL_DETAIL_PAGE + "/edit";
  public static final String GOAL_EDIT_BY_ID = GOAL_EDIT_PAGE + ID;

  // エラーページ
  private static final String ERROR_PAGE = "/error";
  public static final String ERROR_NOT_ACCESSIBLE = ERROR_PAGE + "/not-accessible";
}
