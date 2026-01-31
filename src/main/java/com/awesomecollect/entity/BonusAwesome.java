package com.awesomecollect.entity;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ボーナスえらい！情報を扱うオブジェクト。DBに存在するテーブルと連動する。
 */
@Getter
@Setter
@NoArgsConstructor
public class BonusAwesome {

  private int id;
  private int userId;
  private int awesomePoint;
  private String reason;
  private LocalDate collectedDate;
}
