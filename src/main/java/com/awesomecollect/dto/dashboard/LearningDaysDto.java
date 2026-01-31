package com.awesomecollect.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 学習日数表示用データオブジェクト。
 */
@Data
@AllArgsConstructor
public class LearningDaysDto {

  private int totalDays;
  private int streakDays;
  private String lastLearnedDate;
}
