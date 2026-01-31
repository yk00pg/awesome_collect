package com.awesomecollect.dto.dashboard;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * えらいポイント表示用データオブジェクト。
 */
@Data
@AllArgsConstructor
public class AwesomePointDto {

  private int totalAwesome;
  private List<Integer> splitTotalAwesomeList;
}
