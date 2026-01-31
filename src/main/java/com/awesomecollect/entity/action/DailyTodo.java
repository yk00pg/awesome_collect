package com.awesomecollect.entity.action;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * やること情報を扱うオブジェクト。DBに存在するテーブルと連動する。
 */
@Getter
@Setter
@NoArgsConstructor
public class DailyTodo {

  private int id;
  private int userId;
  private LocalDate date;
  private String content;
  private LocalDateTime registeredAt;
  private LocalDateTime updatedAt;
}
