package com.awesomecollect.entity.action;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 目標情報を扱うオブジェクト。DBに存在するテーブルと連動する。
 */
@Getter
@Setter
@NoArgsConstructor
public class Goal {

  private int id;
  private int userId;
  private String title;
  private String content;
  private boolean achieved;
  private LocalDateTime registeredAt;
  private LocalDateTime updatedAt;
  private LocalDateTime statusUpdatedAt;
}
