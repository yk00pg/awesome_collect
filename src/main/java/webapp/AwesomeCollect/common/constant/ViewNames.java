package webapp.AwesomeCollect.common.constant;

public class ViewNames {

  public static final String SIGNUP_PAGE = "/signup";
  public static final String LOGIN_PAGE = "/login";
  public static final String HOME_PAGE = "/home";

  public static final String MY_PAGE = "/mypage";
  public static final String MY_PAGE_EDIT = "/mypage/edit";
  public static final String MY_PAGE_CHANGE_PASSWORD = "/mypage/change_password";

  private static final String DATE = "/{date}";

  public static final String TODO_PAGE = "/todo";
  public static final String TODO_EDIT_PAGE = "/todo/edit";
  public static final String DAILY_TODO_VIEW_PAGE = TODO_PAGE + DATE;
  public static final String DAILY_TODO_EDIT_PAGE = TODO_EDIT_PAGE + DATE;

  public static final String DONE_PAGE = "/done";
  public static final String DONE_EDIT_PAGE = "/done/edit";
  public static final String DAILY_DONE_VIEW_PAGE = DONE_PAGE + DATE;
  public static final String DAILY_DONE_EDIT_PAGE = DONE_EDIT_PAGE + DATE;
}
