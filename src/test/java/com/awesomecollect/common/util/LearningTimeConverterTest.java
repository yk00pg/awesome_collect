package com.awesomecollect.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LearningTimeConverterTest {

  @Test
  void toTotalMinutes_学習時間を分単位に変換して返す() {
    int hours = 5;
    int minutes = 45;
    int result = LearningTimeConverter.toTotalMinutes(hours, minutes);

    assertEquals(345, result, "分単位に変換した学習時間が返る");
  }

  @Test
  void toHoursPart_分単位の合計学習時間のうち時間単位部分を算出して返す() {
    int totalMinutes = 345;
    int result = LearningTimeConverter.toHoursPart(totalMinutes);

    assertEquals(5, result, "合計学習時間のうち時簡単位部分が返る");
  }

  @Test
  void toMinutesPart_分単位の合計学習時間うち時間単位部分を除いた残りを算出して返す() {
    int totalMinutes = 345;
    int result = LearningTimeConverter.toMinutesPart(totalMinutes);

    assertEquals(45, result, "合計学習時間のうち分単位部分が返る");
  }

  @Test
  void formatAsJpString_合計学習時間を時間と分に分けて単位をつけて返す() {
    int totalMinutes = 345;
    String result = LearningTimeConverter.formatAsJpString(totalMinutes);

    assertEquals("5時間45分", result, "単位をつけた合計学習時間が返る");
  }

  @Test
  void formatAsJpString_時間単位部分が0のときは時間は省く() {
    int totalMinutes = 45;
    String result = LearningTimeConverter.formatAsJpString(totalMinutes);

    assertEquals("45分", result, "単位をつけた合計学習時間（分のみ）が返る");
  }

  @Test
  void formatAsJpString_分単位部分が0のときは分は省く() {
    int totalMinutes = 300;
    String result = LearningTimeConverter.formatAsJpString(totalMinutes);

    assertEquals("5時間", result,"単位をつけた合計学習時間（時間のみ）が返る");
  }
}