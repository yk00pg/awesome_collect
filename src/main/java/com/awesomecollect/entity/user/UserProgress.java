package com.awesomecollect.entity.user;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ユーザー進捗状況を扱うオブジェクト。DBに存在するテーブルと連動する。
 */
@Getter
@Setter
@NoArgsConstructor
public class UserProgress {

  private int userId;
  private LocalDate registeredDate;
  private int totalActionDays;
  private LocalDate lastActionDate;
  private int currentStreak;
  private int longestStreak;
  private int streakBonusCount;
}
