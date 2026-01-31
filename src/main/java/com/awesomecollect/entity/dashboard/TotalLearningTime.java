package com.awesomecollect.entity.dashboard;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * タグ別学習時間を扱うオブジェクト。
 */
@Data
@AllArgsConstructor
public class TotalLearningTime {

  private LocalDate date;
  private int totalTime;
}
