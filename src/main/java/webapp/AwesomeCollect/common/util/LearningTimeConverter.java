package webapp.AwesomeCollect.common.util;

/**
 * 学習時間の単位や表記を変換するコンバータークラス。
 */
public class LearningTimeConverter {

  private static final int MINUTES = 60;
  private static final String HOURS_JP_STRING = "時間";
  private static final String MINUTES_JP_STRING = "分";

  // 学習時間を分単位に変換
  public static int toTotalMinutes(int hours, int minutes){
    return (hours * MINUTES) + minutes;
  }

  // 学習時間(分)のうち時間単位部分を算出
  public static int toHoursPart(int totalMinutes){
    return totalMinutes / MINUTES;
  }

  // 学習時間(分)から時間単位部分を除いた残りの分を算出
  public static int toMinutesPart(int totalMinutes){
    return totalMinutes % MINUTES;
  }

  // 学習時間に単位をつける
  public static String formatAsJpString(int totalMinutes){
    int hours = toHoursPart(totalMinutes);
    int remainingMinutes = toMinutesPart(totalMinutes);
    return (hours > 0 ? hours + HOURS_JP_STRING : "")
        + (remainingMinutes > 0 ? remainingMinutes + MINUTES_JP_STRING : "");
  }
}
