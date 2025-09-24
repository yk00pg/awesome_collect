package webapp.AwesomeCollect.common.util;

/**
 * 学習時間の単位や表記を変換するクラス。
 */
public final class LearningTimeConverter {

  private static final int MINUTES = 60;
  private static final String HOURS_JP_STRING = "時間";
  private static final String MINUTES_JP_STRING = "分";

  /**
   * 学習時間をまとめて分単位に変換する。
   *
   * @param hours 学習時間（時間）
   * @param minutes 学習時間（分）
   * @return  合計学習時間（分単位）
   */
  public static int toTotalMinutes(int hours, int minutes){
    return (hours * MINUTES) + minutes;
  }

  /**
   * 合計学習時間（分単位）のうち、時間単位部分を算出する。
   *
   * @param totalMinutes  合計学習時間（分単位）
   * @return  学習時間（時間）
   */
  public static int toHoursPart(int totalMinutes){
    return totalMinutes / MINUTES;
  }

  /**
   * 合計学習時間（分単位）のうち、時間単位部分を除いた残りの分（分単位部分）を算出する。
   *
   * @param totalMinutes  合計学習時間（分単位）
   * @return  学習時間（分）
   */
  public static int toMinutesPart(int totalMinutes){
    return totalMinutes % MINUTES;
  }

  /**
   * 合計学習時間（分単位）を時間と分に分け、単位をつける。
   *
   * @param totalMinutes  合計学習時間（分単位）
   * @return  単位つきの学習時間（時間・分）
   */
  public static String formatAsJpString(int totalMinutes){
    int hours = toHoursPart(totalMinutes);
    int remainingMinutes = toMinutesPart(totalMinutes);
    return (hours > 0 ? hours + HOURS_JP_STRING : "")
        + (remainingMinutes > 0 ? remainingMinutes + MINUTES_JP_STRING : "");
  }
}
