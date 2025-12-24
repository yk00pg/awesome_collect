package webapp.AwesomeCollect.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DateTimeFormatUtilTest {

  @Test
  void formatDate_日付を日本語形式で返す() {
    LocalDate date = LocalDate.of(2025,12,24);
    String result = DateTimeFormatUtil.formatDate(date);

    assertEquals(
        "2025年12月24日 (水)",
        result,
        "日本語形式(yyyy年MM月dd日 (E))にフォーマットされる");
  }

  @Test
  void formatDateTime_日時を指定形式で返す() {
    LocalDateTime dateTime = LocalDateTime.of(2025,12,24,15,30,30);
    String result = DateTimeFormatUtil.formatDateTime(dateTime);

    assertEquals("2025-12-24 15:30:30",
        result,
        "yyyy-MM-dd HH:mm:ss形式にフォーマットされる");
  }

  @Test
  void formatDateTime_日時がnullの場合はnullを返す() {
    String result = DateTimeFormatUtil.formatDateTime(null);

    assertNull(result);
  }
}