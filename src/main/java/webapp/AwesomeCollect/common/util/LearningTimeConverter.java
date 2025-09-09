package webapp.AwesomeCollect.common.util;

public class LearningTimeConverter {

  private static final int MINUTES = 60;
  private static final String HOURS_JP_STRING = "時間";
  private static final String MINUTES_JP_STRING = "分";
  private static final String HOURS_ENG_STRING = "h";
  private static final String MINUTES_ENG_STRING = "m";

  public static int toTotalMinutes(int hours, int minutes){
    return (hours * MINUTES) + minutes;
  }

  public static int toHoursPart(int totalMinutes){
    return totalMinutes / MINUTES;
  }

  public static int toMinutesPart(int totalMinutes){
    return totalMinutes % MINUTES;
  }

  public static String formatAsJpString(int totalMinutes){
    int hours = toHoursPart(totalMinutes);
    int remainingMinutes = toMinutesPart(totalMinutes);
    return (hours > 0 ? hours + HOURS_JP_STRING : "")
        + (remainingMinutes > 0 ? remainingMinutes + MINUTES_JP_STRING : "");
  }

  public static String formatAsEngString(int totalMinutes){
    int hours = toHoursPart(totalMinutes);
    int remainingMinutes = toMinutesPart(totalMinutes);
    return (hours > 0 ? hours + HOURS_ENG_STRING : "")
        + (remainingMinutes + MINUTES_ENG_STRING);
  }
}
