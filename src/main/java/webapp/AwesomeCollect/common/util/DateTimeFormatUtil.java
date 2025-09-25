package webapp.AwesomeCollect.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 日付の表示形式を整形するクラス。
 */
public final class DateTimeFormatUtil {

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy年MM月dd日 (E)", Locale.JAPANESE);

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private DateTimeFormatUtil(){
      // インスタンス化を防止
  }

  public static String formatDate(LocalDate date){
    return date.format(DATE_FORMATTER);
  }

  public static String formatDateTime(LocalDateTime dateTime){
    return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
  }
}
