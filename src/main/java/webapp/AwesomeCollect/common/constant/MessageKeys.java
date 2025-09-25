package webapp.AwesomeCollect.common.constant;

/**
 * メッセージキーの定数クラス。
 */
public final class MessageKeys {

  // exception
  public static final String DUPLICATE_LOGIN_ID = "duplicate.loginId";
  public static final String DUPLICATE_EMAIL = "duplicate.email";
  public static final String CURRENT_PASSWORD_INCORRECT = "currentPassword.incorrect";

  // validation
  public static final String PASSWORD_MISMATCH = "password.mismatch";
  public static final String CURRENT_PASSWORD_BLANK = "currentPassword.blank";
  public static final String CONTENT_BLANK = "content.blank";
  public static final String CONTENT_DUPLICATE = "content.duplicate";
  public static final String DATE_FUTURE = "date.future";
  public static final String LEARNING_TIME_BLANK = "learning.time.blank";
  public static final String TOTAL_LEARNING_TIME_EXCEEDED = "total.learning.time.exceed";
  public static final String GOAL_ALREADY_ACHIEVED = "goal.already.achieved";

  // alert
  public static final String LOGIN_FAILURE = "login.failure";
  public static final String LOGIN_FAILURE_HINT = "login.failure.hint";

  // success
  public static final String SIGNUP_SUCCESS = "signup.success";
  public static final String USERINFO_EDIT_SUCCESS = "userInfo.edit.success";
  public static final String PASSWORD_CHANGE_SUCCESS = "password.change.success";
  public static final String REGISTER_SUCCESS = "register.success";
  public static final String UPDATE_SUCCESS = "update.success";
  public static final String DELETE_SUCCESS = "delete.success";

  // awesome
  public static final String TODO_AWESOME = "todo.awesome";
  public static final String DONE_AWESOME = "done.awesome";
  public static final String GOAL_AWESOME = "goal.awesome";
  public static final String ACHIEVED_AWESOME = "achieved.awesome";
  public static final String MEMO_AWESOME = "memo.awesome";
  public static final String ARTICLE_AWESOME = "article.awesome";
  public static final String FINISHED_AWESOME = "finished.awesome";

  private MessageKeys(){
    // インスタンス化を防止
  }
}
